FROM tomcat:8-jre8-alpine

ADD dist/Artificial.war /usr/local/tomcat/webapps/ROOT.war

RUN rm -rf /usr/local/tomcat/webapps/ROOT/*; cd /usr/local/tomcat/webapps/ROOT; unzip ../ROOT.war; rm ../ROOT.war


