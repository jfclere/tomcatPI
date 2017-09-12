FROM armv7/armhf-fedora

USER root

RUN mkdir -p /deployments

# JAVA_APP_DIR is used by run-java.sh for finding the binaries
ENV JAVA_APP_DIR=/deployments

# /dev/urandom is used as random source, which is prefectly safe
# according to http://www.2uo.de/myths-about-urandom/
#RUN yum install -y \
#       java-1.8.0-openjdk  \
#       java-1.8.0-openjdk-devel \
#RUN  echo "securerandom.source=file:/dev/urandom" >> /usr/lib/jvm/java/jre/lib/security/java.security

ADD jdk-8u121-linux-arm32-vfp-hflt.tar.gz /root

RUN ln -s /root/jdk1.8.0_121/jre/ /etc/alternatives/jre
RUN ln -s /root/jdk1.8.0_121/jre/bin/java /etc/alternatives/java
RUN ln -s /etc/alternatives/java /usr/bin/java

COPY tomcat-in-the-cloud-1.0-SNAPSHOT.jar /deployments

ENV JAVA_HOME /etc/alternatives/jre
ENV OPENSHIFT_KUBE_PING_NAMESPACE default

# Agent bond including Jolokia and jmx_exporter
ADD agent-bond-opts /opt/run-java-options
RUN mkdir -p /opt/agent-bond \
 && curl http://central.maven.org/maven2/io/fabric8/agent-bond-agent/0.1.4/agent-bond-agent-0.1.4.jar \
          -o /opt/agent-bond/agent-bond.jar \
 && chmod 444 /opt/agent-bond/agent-bond.jar \
 && chmod 755 /opt/run-java-options
ADD jmx_exporter_config.yml /opt/agent-bond/
EXPOSE 8080 4000

# Add run script as /deployments/run-java.sh and make it executable
COPY run-java.sh debug-options container-limits java-default-options /deployments/
RUN chmod 755 /deployments/run-java.sh /deployments/java-default-options /deployments/container-limits /deployments/debug-options




CMD [ "/deployments/run-java.sh" ]
