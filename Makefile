clean:
	@mvn clean

install: clean
	@mvn install -Dmaven.test.skip=true