/*
 * Copyright (C) 2017 SailPoint Technologies, Inc.  All rights reserved.
 */

// Load the shared release utils which provides several SailPoint specific commands such as
// drydockDeploy() and createDeployTicket. The @ sign specifies a specific tag within the
// shared library to use. Note: The "_" underscore is not a typo.
// https://github.com/sailpoint/jenkins-release-utils
@Library('sailpoint/jenkins-release-utils@1.3.1')_

/**
 * Jenkins release pipeline for the wgms service.
 */
pipeline {
    agent none

    options {
        // Keep the last 50 builds
        buildDiscarder(logRotator(numToKeepStr: '50'))
    }

    triggers {
        // Poll for changes every 5 minutes.
        pollSCM('H/5 * * * *')
    }

    environment {
        // The scrum which owns this component
        JIRA_PROJECT = 'IDNIPANEMA'

        // The component name in Jira for the deployment ticket
        JIRA_COMPONENT = 'WGMS'

        // The name of the build artifact to generate
        BUILD_NUMBER = ${env.BUILD_NUMBER}

        // Which room to report successes & failures too.
        HIPCHAT_ROOM = "Ipanema"

        // The branch releases can be cut from.
        RELEASE_BRANCH = "master"

        // The type of service being released
        SERVICE_TYPE = "wgms"

        // The e2e test suite to execute on validation steps
        E2E_TEST_SUITE = "src/test/resources/test_suites/ipanema/ipanema-wgms-suite.xml"

        // The maximum amount of time (in minutes) for tests to take before they are auto failed.
        TEST_TIMEOUT = 90
    }

    stages {
        stage('Build') {
            when {
                branch env.RELEASE_BRANCH
            }
            steps {
                // Once a milestone is reached no older builds are allowed to pass.
                milestone(ordinal: 100, label:'Build')

                echo "${env.SERVICE_TYPE} service release pipeline for ${env.BUILD_NUMBER} is starting."
                hipchatSend(
                        failOnError: false,
                        notify: false,
                        room: "${env.HIPCHAT_ROOM}",
                        message: "${env.SERVICE_TYPE} service release pipeline for <a href='${env.BUILD_URL}'>${env.BUILD_NUMBER}</a> is starting.",
                        color: 'YELLOW'
                )

                script {
                    // run on an ec2 worker node
                    node {
                        label 'devaws'
                        env.JAVA_HOME="${tool 'jdk-8u152'}"
                        env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
                        sh 'java -version'

                        try {
                            // Checkout code from repository. NOTE: This will be defined in the Jenkins job
                            checkout scm

                            echo "Starting build of ${env.SERVICE_TYPE}"
                            // build wgms with gradlew
                            atlasServiceBuild([
                                    service_name: 'wgms',
                                    version: env.BUILD_NUMBER,
                                    timeout: 20
                            ])
                        } finally {
                            // Always cleanup the workspace
                            deleteDir()
                        }
                    }
                }
            }
        }
        stage('Bermuda Verify') {

            when {
                branch env.RELEASE_BRANCH
            }

            agent {
                label 'devaws'
            }

            steps {
                echo "Deploying ${env.SERVICE_TYPE} build ${env.BUILD_NUMBER} to bermuda."
                script {
                    hipchatSend(
                            failOnError: false,
                            notify: false,
                            room: "${env.HIPCHAT_ROOM}",
                            message: "Deploy of ${env.SERVICE_TYPE} <a href='${env.BUILD_URL}'>${env.BUILD_NUMBER}</a> to bermuda is starting.",
                            color: 'YELLOW'
                    )

                    drydockDeploy([
                            timeout      : 20,
                            drydock_host : 'slipway.infra.identitynow.com',
                            pod          : 'bermuda',
                            module       : 'wgms',
                            version      : env.BUILD_NUMBER
                    ])

                    def attempts = 0
                    waitUntil {
                        try {
                            try {
                                // Test the build on bermuda
                                timeout(time: Integer.valueOf(env.TEST_TIMEOUT), unit: 'MINUTES') {
                                    echo "Verify build ${env.BUILD_NUMBER} on bermuda"
                                    atlasServiceTest([
                                            test_branch: 'develop',
                                            test_org: 'ipanema-bermuda',
                                            pod: 'bermuda',
                                            test_suite: "${E2E_TEST_SUITE}"
                                    ])
                                }
                            } finally {
                                deleteDir()
                            }
                            return true
                        } catch (error) {
                            echo "bermuda verification attempt failed with error ${error}"
                            attempt++

                            hipchatSend(
                                    failOnError: false,
                                    notify: true,
                                    room: "${env.HIPCHAT_ROOM}",
                                    message: "${env.SERVICE_TYPE} release pipeline for <a href='${env.BUILD_URL}/input'>${env.BUILD_NUMBER}</a> failed on bermuda ${attempts} time(s), should I try again?<br/>Error: ${error}",
                                    color: 'RED'
                            )
                            input (
                                    message: "bermuda verification failed ${attempts} time(s), should I retry the tests?",
                                    submitter: env.RETRY_SUBMITTER
                            )
                            return false
                        }
                    }
                }
            }
        }
        stage('Deploy Build') {
            when {
                branch env.RELEASE_BRANCH
            }
            agent {
                label 'devaws'
            }

            steps {

                script {
                    def pods = [
                            "capri",
                            "dev01-useast1",
                            "lighthouse"
                    ]

                    def stepsForParallel = [:]
                    for (int i = 0; i < pods.size(); i++) {
                        def pod = pods[i]
                        def delay = i*2
                        stepsForParallel["Deploy to ${pod}"] = { ->

                            // Drydock doesn't like you spamming it.
                            sleep(time: delay, unit: 'SECONDS')

                            echo "Deploy to ${pod}..."
                            drydockDeploy([
                                    timeout      : 20,
                                    drydock_host : 'slipway.infra.identitynow.com',
                                    pod          : pod,
                                    module       : 'wgms',
                                    version      : env.BUILD_NUMBER
                            ])
                            echo "Completed deploy to ${pod}"
                        }
                    }

                    echo "Deploying to pods: ${pods}"

                    parallel stepsForParallel

                    echo "Completed deployment to all pods: ${pods}"
                }
            }
        }
        stage('Lighthouse Verify') {
            when {
                branch env.RELEASE_BRANCH
            }
            steps {
                script {
                    def attempts = 0
                    waitUntil {
                        try {
                            node {
                                label 'devaws'
                                try {
                                    // Test the build on lighthouse
                                    timeout(time: Integer.valueOf(env.TEST_TIMEOUT), unit: 'MINUTES') {
                                        echo "Verify build ${env.BUILD_NUMBER} on lighthouse"
                                        atlasServiceTest([
                                                test_branch: 'develop',
                                                test_org: 'ipanema-light',
                                                pod: 'lighthouse',
                                                test_suite: "${E2E_TEST_SUITE}"
                                        ])
                                    }
                                } finally {
                                    deleteDir()
                                }
                            }
                            return true
                        } catch(error) {
                            echo "Lighthouse verification attempt failed with error: ${error}"
                            attempts++

                            hipchatSend(
                                    failOnError: false,
                                    notify: true,
                                    room: "${env.HIPCHAT_ROOM}",
                                    message: "${env.SERVICE_TYPE} release pipeline for <a href='${env.BUILD_URL}/input'>${env.BUILD_NUMBER}</a> failed on lighthouse ${attempts} time(s), should I try again?<br/>Error: ${error}",
                                    color: 'RED'
                            )
                            input (
                                    message: "lighthouse verification failed ${attempts} time(s), should I retry the tests?",
                                    submitter: env.RETRY_SUBMITTER
                            )
                            return false
                        }
                    }
                }
            }
        }
        stage('Create Deployment Ticket') {
            when {
                branch env.RELEASE_BRANCH
            }

            agent {
                label 'devaws'
            }

            steps {

                // Once a milestone is reached no older builds are allowed to pass.
                milestone(ordinal: 200, label:'Create Jira Ticket')

                script {
                    // Capitalize the module type, grail's .capatilize() is not allowed in the sandbox.
                    def jiraName = env.SERVICE_TYPE.substring(0, 1).toUpperCase() + env.SERVICE_TYPE.substring(1).toLowerCase()

                    createDeployTicket([
                            jira_project  : env.JIRA_PROJECT,
                            jira_name     : jiraName,
                            jira_component: env.JIRA_COMPONENT,
                            deploy_app    : 'wgms',
                            deploy_name   : env.SERVICE_TYPE.toLowerCase(),
                            deploy_version: env.BUILD_NUMBER,
                            approval_email: env.APPROVAL_EMAIL
                    ])
                }

                echo "All done, deployment is ready for approval"
            }
        }
    }

    post {
        success {
            hipchatSend(
                    failOnError: false,
                    notify: true,
                    room: "${env.HIPCHAT_ROOM}",
                    message: "${env.SERVICE_TYPE} release pipeline for <a href='${env.BUILD_URL}'>${env.BUILD_NUMBER}</a> was successfull.",
                    color: 'GREEN'
            )
        }
        failure {
            hipchatSend(
                    failOnError: false,
                    notify: true,
                    room: "${env.HIPCHAT_ROOM}",
                    message: "${env.SERVICE_TYPE} release pipeline for <a href='${env.BUILD_URL}'>${env.BUILD_NUMBER}</a> failed.",
                    color: 'RED'
            )
        }
        aborted {
            hipchatSend(
                    failOnError: false,
                    notify: true,
                    room: "${env.HIPCHAT_ROOM}",
                    message: "${env.SERVICE_TYPE} release pipeline for <a href='${env.BUILD_URL}'>${env.BUILD_NUMBER}</a> aborted.",
                    color: 'RED'
            )
        }
    }
}
