FROM arm32v7/openjdk
VOLUME /tmp

ADD target/Jira-0.0.1-SNAPSHOT.jar Jira-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar", "Jira-0.0.1-SNAPSHOT.jar"]
