FROM 406205545357.dkr.ecr.us-east-1.amazonaws.com/sailpoint/java:2

EXPOSE 7100

COPY server /server
RUN chmod a+x /server/bin/server

WORKDIR /server/bin

ENV ATLAS_PRODUCTION true

ENV WMS_SERVER_OPTS "-Dlog4j.configurationFile=log4j2-production.xml"

CMD exec ./server