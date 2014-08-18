gwt_spring_hibernate_sample
===========================

Sample project with GWT+Spring+Hibernate+Maven usage.

This project contains simple tree-like structure editor with GWT 2.6 on frontend,
Spring JSON REST service and data model, made with Hibernate.

To create sample db, type "pg_restore db_name create_sample_db.pg_dump".

Then setup db connection in hibernate.conf.

To build, type "mvn package".
Then deploy .war to application server and go to "localhost:8080/knowledgebase/" .
