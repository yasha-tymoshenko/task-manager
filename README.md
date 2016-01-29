# task-manager
Lists running processes #JavaFx #import #export #xml #excel #win32 #java8

1
The application shall be written and build in JAVA.

2
The application shall have a graphical user interface implemented with JavaFX.

3
The application shall be executable on Microsoft Windows.

4
The application shall have a function that runs “tasklist” as a console command.
The output of this command shall be returned to the application and show the following information
(of each task, sorted by used memory) within the graphical user interface:
Name
PID
Used Memory

5
The application shall provide a manner of handling different OS language settings
which might affect the result of the “tasklist” command.

6
The application shall have a function that removes any duplicates from the listed tasks
(assuming the name is the identifier – ignoring the PID).
Tasks of the same name shall be grouped together and the used memory aggregated.

7
The application shall have a function that exports the cleaned list of tasks (see Req_005)
into a XML file abiding by the following scheme:
<tasks>
    <task>
        <name></name>
        <memory></memory>
    </task>
</tasks>

9
The application shall utilize a SaveFileDialog form (or something equivalent)
to give the user a method to choose the filename and location of the new xml file.

9
The application shall have a function to re-import the saved XML file and compare the contents
to the data shown in the graphical user interface. Any changes shall be shown in the GUI.

10
The application shall provide a function that exports the gathered data into Microsoft Excel
and generate a chart about the memory usage of tasks.
