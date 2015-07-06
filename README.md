*Repository URL: https://github.com/partnet/ads-proto*

*Repository Branch: pool3-dev*

*Prototype URL: http://ads-pool3.part.net/#*

The following description makes references to evidence within the repository of Partnet's adherence
to the Playbook guidance. Paths to referenced documents can be found in the Attachment E submitted
through GSA eBuy. Superscripts <sup>(a-q)</sup> highlight where we have addressed the specific
corresponding requirement. 

The Partnet full-stack prototype provides a front-end, web-based application, Check My Meds, that
allows users to input a drug name and demographic information and retrieve a visualization of the
likelihood of potential side effects and outcomes identified within the FDA adverse events data.

Additionally, the prototype provides three back-end REST APIs to highlight our capability to
provide services similar to OpenFDA, to demonstrate server-side processing, and to provide an API
with augmented information enabling an enhanced interface:

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

A multi-disciplinary team, led by our Product Manager <sup>(a)</sup>, was assembled to include an
Interaction Designer / User Researcher / Usability Tester, Visual Designer, a Technical Architect, 
Frontend and Backend developers, and DevOps engineers. In addition, staff that were not related
to the project were engaged to serve as the end users (i.e., People). Please reference the document
“Pool 3 Team Roster”.<sup>(b)</sup>

Scrum was used as the development methodology. The team originally planned a total of four Sprints
(including Sprint 0). Sprint 3 was originally planned to be half a day longer than the other
Sprints rather than have a half-day Sprint. However, when the proposal submission completion date
was extended on June 24, the team decided to continue with one day Sprints and plan for a total of
seven Sprints. The team had working software at the end of each Sprint as proven by the Sprint
demos. Our users prioritized new functionality over the relatively minor issues that were filed
in the GitHub issue tracker. Therefore, functionality was the main focus through Sprint 5.
Sprint 6 was used to work on the issues in the issue tracker. Please refer to the “Sprint
Documentation” directory in the root of the repository for Sprint artifacts. The Product Backlog
was defined in Sprint 0. The planning meetings for Sprints 1-6 defined the Sprint Backlogs. Sprints
included a Planning Meeting to begin the Sprint and a Demo and Retrospective at the end of the
Sprint. Each day the team had a daily stand-up meeting.

The prototype design process included potential users to help us understand what people want and
need from a product like the Check My Meds site.<sup>(c)</sup> Please see the Visual Design,
Interaction Design, and Usability Testing directory for the “Co-Creation Session” documentation.
Once user pain points and preferences were identified, the team outlined and created a
user-friendly, responsive site to address their needs. We designed the site with a mobile-first
mindset, meaning that the user experience is high on any device or viewport (from mobile phone or
tablet, to full-screen desktop monitor and laptop experiences). 

To tailor our site to our users, we used numerous human-centered design techniques<sup>(d)</sup>
and tools, including iterative design (see “Iterative Design Process” document)<sup>(h)</sup>,
focus groups (see “Focus Group 50 and Over Users and Paper Prototyping” document), prototyping (see
“Co-Creation Session” document), participatory design (see “Co-Creation Session” document),
task analysis (see “Task Analysis” document), and usability testing (see “Usability Testing
Survey and Results” document).<sup>(f)</sup> To speed development, ensure consistency, and
maximize usability, we implemented a design style guide and a pattern library (see “Style Guide
and Pattern Library” document).<sup>(e)</sup>

At every step in the design process and in each Sprint, we worked with the pool of potential
users to gather feedback and iterate on our design<sup>(g)</sup> (see “Iterative Design Process” document,
“Co-Creation Session” document, “Focus Group 50 and over Users and Paper Prototyping” document).
Overall site usability and pain points were recorded and used to shape the product into something
everyday people would want to use (see “Task Analysis” document and “Usability Testing Survey and
Results” document).<sup>(d)</sup>

Partnet’s technical implementation highlights a broad array of modern, freely available open source
technologies.<sup>(i)</sup> Notable components include three modern, front-end technologies:

- AngularJS
- The D3 Visualization library
- Twitter’s Bootstrap reactive CSS library

These were complimented by with several additional backend components:

- Java 8
- Java API for RESTful Web Services (JAX-RS)
- Wildfly application server
- Elasticsearch

AngularJS is a widely used application framework for building cross-platform applications.
Javascript is the lingua franca for web development supported by an Ecma standard
(aka EcmaScript) and all leading web browser vendors. In addition to widely used Javascript
development utilities, Partnet adopted the D3 visualization library to highlight the emerging
visualization capabilities of modern, SVG-enabled browsers. To support a reactive design on a
mobile platform, Partnet utilized Twitter Bootstrap styling with Less CSS customization. In
addition, the Apache Cordova libraries were used to generate a mobile Android
application.<sup>(h)</sup> The full set of Free Open Source Software is captured in the
“FOSS Software” spreadsheet.<sup>(q)</sup>

Full stack development was supported with Partnet’s standing agile infrastructure, described in the
document “Continuous Delivery Pipeline”. Git is used as the version control system. Jenkins,
Gradle, and Artifactory are used to perform automated, controlled, tested, and versioned
builds.<sup>(l)</sup> 

The Q4 2014 FAERS data set (ADR14Q4.xml) was downloaded and indexed into an Elasticsearch instance
after validating numeric data fields and and normalizing units of measure.

A modified agile scrum process was followed for Pool 3 development to enable iterative development.
The Technical Architect conducted daily stand-up meetings as well as planning meetings for each
Sprint. The sprint duration lasted one day. Seven sprints were completed.  Bugs were filed and
assigned to sprints using milestones. See repository issues labeled pool3.  Pool3 design was
updated after the User Co-Creation session to include drug typeahead feature which was completed in
Sprint 6. See the “Co-Creation Session” document.<sup>(g)</sup>

Unit tests can be found in the repository under client/test and server/src/test.<sup>(k)</sup>

The prototype is deployed to an Amazon EC2 instance running a CentOS 6.6 image.<sup>(j)</sup>
The environment was configured with an application account and corresponding sudo permissions.
An NGINX proxy server was installed along with a security profile. A DNS CNAME entry of
ads-pool3.part.net was made for the corresponding IP address.

Automated tools are used to ensure reproducible deployments and consistently managed application
configuration.<sup>(m)</sup> The AWS application environment is provisioned using Ansible into a
Docker container<sup>(o)</sup>. Gradle is utilized to build and bundle an archive containing the
web application and REST services for deployment to the Wildfly application server with the
Gradle Cargo plugin. 

Partnet is using Nagios® Core as its primary monitoring platform. The monitoring is broken up into
3 categories: server monitoring, process monitoring, and accessibility monitoring. A number of
specific security measures are also present in the environment (see “Continuous Monitoring”
document for details).<sup>(n)</sup>

Please find installation instructions to install and run the prototype on an alternate machine in
the “Installation Instructions” document.<sup>(p)</sup>
