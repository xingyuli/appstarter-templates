FROM docker.mobisist.com/mobisist/tomcat:8-standalone

RUN apt-get install unzip zip

ARG PROFILE

COPY build/libs/*.war $TOMCAT_HOME/webapps/portal.war

RUN cd $TOMCAT_HOME/webapps && \
    unzip -q portal.war -d temp && \
    cd temp && \
    sed -i "1i spring.profiles.active: ${PROFILE}" WEB-INF/classes/config/application.yaml && \
    zip -q -r temp.war . && \
    mv -f temp.war $TOMCAT_HOME/webapps/portal.war && \
    cd .. && \
    rm -rf temp

ENV JPDA_ADDRESS="8000"
ENV JPDA_TRANSPORT="dt_socket"

EXPOSE 8080 8000
VOLUME /var/opt/upload
ENTRYPOINT $TOMCAT_HOME/bin/catalina.sh jpda run
