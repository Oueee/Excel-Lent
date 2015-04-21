"""Module manage.

"""

import sys
import os
import subprocess
import launcher
import shutil 

attributs = {
    'flags': '-g',# -Xlint:all',
    'main_class': 'gui.GUI',
}

### compile parts
def build(self):
    if not os.path.exists(os.path.join(".", "bin")):
        os.mkdir("bin")
    subprocess.call(['javac -d bin -sourcepath src/main src/main/gui/GUI.java'], shell=True)

### run parts
def run(self):
    subprocess.call(['java -classpath bin gui.GUI'], shell=True)

def clean(self):
    """clean the java project"""

    self._clean(os.path.join('bin', '*'),
                os.path.join('tree', '*'))

    self._clean_rec('*~')

launcher.main()
