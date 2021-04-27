mvn package -Dmaven.test.failure.ignore=true

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file \
  -Dfile=./target/efs-task9-todo-app-1.1.0-SNAPSHOT-tests.jar \
  -DgroupId=efs -DartifactId=efs-task9-todo-app.tests \
  -Dversion=1.1.0-SNAPSHOT -Dclassifier=tests \
  -Dpackaging=test-jar \
  -DcreateChecksum=true \
  -DlocalRepositoryPath=./docs/repo