FROM library/centos:6.6

# Update system and install dependencies
RUN yum update -y
RUN yum install -y epel-release
RUN yum install -y ansible gunzip python-setuptools tar unzip
RUN easy_install supervisor

# Create and run under non-privileged user
RUN groupadd -r ads && useradd -g ads ads
USER ads

# temporarily copy the ansible directory into docker container
ADD ansible /tmp/ansible/
ADD ansible.cfg /tmp/
WORKDIR /tmp

# open ports for wildfly and elasticsearch within the container
EXPOSE 8080 9200 9990

# provision the container
RUN ansible-playbook -vvv ansible/playbooks/docker-build.yml -i ansible/inventory/prod-docker.ini

# remove files created for provisioning
RUN rm -rf /home/ads/archive /home/ads/wildfly/current/standalone/configuration/standalone_xml_history/current
USER root
RUN rm -rf /tmp/ansible*
USER ads

# configure supervisord to allow easily running both elasticsearch and wildfly within the container
COPY supervisord.conf /etc/supervisord.conf
CMD /usr/bin/supervisord
