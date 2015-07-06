*Repository URL: https://github.com/partnet/ads-proto*

*Repository Branch: pool1-dev*

*Prototype URL: http://ads-pool1.part.net/#/client*

The following description makes references to evidence within the repository of Partnet's adherence
to the Playbook guidance. Paths to referenced documents can be found in the Attachment E submitted
through GSA eBuy. Superscripts <sup>(a-k)</sup> highlight where we have addressed the specific
corresponding requirement. 

The Partnet prototype, Check My Meds, allows users to input a drug name and demographic information
and retrieve a visualization of the likelihood of potential side effects and outcomes identified
within the FDA adverse events data.

A multi-disciplinary team, led by our Product Manager <sup>(a)</sup>, was assembled to include an
Interaction Designer / User Researcher / Usability Tester, Visual Designer, and Frontend Web
Developers. In addition, staff that were not related to the project were engaged to serve as the
end users (i.e., People). Please reference the document “Pool 1 Team Roster”.<sup>(b)</sup>

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

To tailor our site to our users, we used numerous human-centered design techniques and tools,
including iterative design (see “Iterative Design Process” document)<sup>(h)</sup>, focus groups
(see “Focus Group 50 and Over Users and Paper Prototyping” document), prototyping (see
“Co-Creation Session” document), participatory design (see “Co-Creation Session” document),
task analysis (see “Task Analysis” document), and usability testing (see “Usability Testing
Survey and Results” document).<sup>(g)</sup> To speed development, ensure consistency, and
maximize usability, we implemented a design style guide and a pattern library (see “Style Guide
and Pattern Library” document).<sup>(e)</sup>

At every step in the design process and in each Sprint, we worked with the pool of potential
users to gather feedback and iterate on our design (see “Iterative Design Process” document,
“Co-Creation Session” document, “Focus Group 50 and over Users and Paper Prototyping” document).
Overall site usability and pain points were recorded and used to shape the product into something
everyday people would want to use (see “Task Analysis” document and “Usability Testing Survey and
Results” document).<sup>(d)</sup>

Partnet’s technical implementation highlights three modern, front-end technologies:

* AngularJS
* The D3 Visualization library
* Twitter’s Bootstrap reactive CSS library

AngularJS is a widely used application framework for building cross-platform applications.
Javascript is the lingua franca for web development supported by an Ecma standard
(aka EcmaScript) and all leading web browser vendors. In addition to widely used Javascript
development utilities, Partnet adopted the D3 visualization library to highlight the emerging
visualization capabilities of modern, SVG-enabled browsers. To support a reactive design on a
mobile platform, Partnet utilized Twitter Bootstrap styling with Less CSS customization. In
addition, the Apache Cordova libraries were used to generate a mobile Android
application.<sup>(i)</sup> The full set of Free Open Source Software is captured in the
“FOSS Software” spreadsheet.<sup>(f,k)</sup>

Please find installation instructions to install and run the prototype on an alternate machine in
the “Installation Instructions” document.<sup>(j)</sup>
