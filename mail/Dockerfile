FROM centos:7

RUN yum install -y epel-release && yum update -y && \
    yum -y install postfix cyrus-sasl-sql perl supervisor

ADD scripts/* /
ADD sasl/smtpd.conf /etc/sasl2/smtpd.conf
ADD postfix/* /
ADD docker_entrypoint.sh /

COPY postfix.ini /etc/supervisord.d/

RUN cat /main.cf.additions >>/etc/postfix/main.cf && \
    cat /master.cf.additions >>/etc/postfix/master.cf && \
    cat /transport.additions >>/etc/postfix/transport

EXPOSE 25

ENTRYPOINT ["/docker_entrypoint.sh"]
