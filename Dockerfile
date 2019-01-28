FROM clojure
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN lein uberjar
CMD ["java", "-jar", "target/doorman-0.1.0-standalone.jar"]
