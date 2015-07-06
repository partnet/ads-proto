*Repository URL: https://github.com/partnet/ads-proto*

*Repository Branch: pool2-dev*

*Prototype URL: http://ads-pool2.part.net/#*

The following description makes references to evidence within the repository of Partnet's adherence
to the Playbook guidance. Paths to referenced documents can be found in Attachment E submitted
through GSA eBuy. Superscripts <sup>(a-l)</sup> highlight where we have addressed corresponding
requirements.

The Partnet prototype provides three REST APIs to highlight our capability to provide services
similar to OpenFDA, to demonstrate server-side processing, and to provide an API with augmented
information enabling an enhanced interface:

* An API to support typeahead search (incremental search) by retrieving a list of drugs given a
  search term or prefix.
* An API to query the indications for a given drug, the count of their occurrence, and the average
  time between taking a drug and reaction onset.
* An API to query a list of reactions to a specified drug. For each reaction, the occurrence counts
  and a calculated PRR (Proportional Reporting Ratio) and ROR (Reporting Odds Ratio) are provided,
  along with the outcomes of the reaction and their associated counts.

The prototype user interface includes a table highlighting information from the extended APIs: 

* PRR and ROR fields were added. Visualizations of reaction occurrence rates also use the PRR for
  scaling, when available.
* Indications and counts were added for each drug.
* Average, minimum, and maximum time of onset of reaction were added.

We assembled a multi-disciplinary team (see “Pool 2 Team Roster”)<sup>(b)</sup>, led by our
Technical Architect<sup>(a)</sup>, including frontend and backend developers, and DevOps engineers
to support the continuous delivery pipeline, continuous monitoring, provisioning, and EC2
deployment.

A broad array of modern, freely available open source technologies were used for the prototype
(see the “FOSS Software” spreadsheet).<sup>(l)</sup>  Notable components include:<sup>(c)</sup>

- AngularJS
- Java 8
- Java API for RESTful Web Services (JAX-RS)
- Wildfly application server
- Elasticsearch

Development was supported with Partnet’s standing agile infrastructure, described in the document
“Continuous Delivery Pipeline”. Git is used as the version control system. Jenkins, Gradle, and
Artifactory are used to perform automated, controlled, tested, and versioned builds.<sup>(f)</sup> 

The Q4 2014 FAERS data set (ADR14Q4.xml) was downloaded and indexed into an Elasticsearch instance
after validating numeric data fields and and normalizing units of measure.

A modified agile scrum process was followed for Pool 2 development to enable iterative development.
The Technical Architect conducted daily stand-up meetings as well as planning meetings for each
Sprint. The sprint duration lasted one day. Seven sprints were completed.  Bugs were filed and
assigned to sprints using milestones. See repository issues labeled pool2.  Pool2 design was
updated after the User Co-Creation session to include drug typeahead feature which was completed in
Sprint 6. See the “Co-Creation Session” document.<sup>(j)</sup>

Unit tests can be found in the repository under server/src/test.<sup>(e)</sup>

The prototype is deployed to an Amazon EC2 instance running a CentOS 6.6 image.<sup>(d)</sup>
The environment was configured with an application account and corresponding sudo permissions.
An NGINX proxy server was installed along with a security profile. A DNS CNAME entry of
ads-pool2.part.net was made for the corresponding IP address.

Automated tools are used to ensure reproducible deployments and consistently managed application
configuration.<sup>(g)</sup> The AWS application environment is provisioned using Ansible into a
Docker container<sup>(i)</sup>. Gradle is utilized to build and bundle an archive containing the
web application and REST services for deployment to the Wildfly application server with the
Gradle Cargo plugin. 

Partnet is using Nagios® Core as its primary monitoring platform. The monitoring is broken up into
3 categories: server monitoring, process monitoring, and accessibility monitoring. A number of
specific security measures are also present in the environment (see “Continuous Monitoring”
document for details).<sup>(h)</sup>

Please find installation instructions to install and run the prototype on an alternate machine in
the “Installation Instructions” document.<sup>(k)</sup>
