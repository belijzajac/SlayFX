SRC  = $(shell pwd)/src
EXEC = java com.slayfx.gui.Main

all:
	# Building...
	$(shell find $(SRC) -type f -name *.java -print | xargs javac)

run:
	cd $(SRC) && $(EXEC)

clean:
	# Cleaning...
	$(shell find $(SRC) -name "*.class" -delete)
