FROM 406205545357.dkr.ecr.us-east-1.amazonaws.com/sailpoint/java:2

EXPOSE 7100

COPY wgms-server /wgms-server
RUN chmod a+x /wgms-server/bin/wgms-server

WORKDIR /wgms-server/bin

ENV ATLAS_PRODUCTION true

ENV WGMS_SERVER_OPTS "-Dlog4j.configurationFile=log4j2-production.xml"

CMD exec ./wgms-server