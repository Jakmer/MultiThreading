JAVAC = javac
JAVA = java
JFLAGS = --module-path /home/jakub/Documents/Workspace/Java/javafx-sdk-20.0.1/lib --add-modules javafx.controls

.SUFFIXES: .java .class
.java.class:
	$(JAVAC) $(JFLAGS) $*.java

CLASSES = \
        Main.java \
        Gui.java \
		MyRectangle.java \

MAIN = Main

default: classes

classes: $(CLASSES:.java=.class)

run: classes
	@read -p "Enter arguments: " ARG1 ARG2 ARG3 ARG4; \
	$(JAVA) $(JFLAGS) $(MAIN) $$ARG1 $$ARG2 $$ARG3 $$ARG4

clean:
	$(RM) *.class