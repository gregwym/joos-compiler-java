#
# define ant build command
#

ANT = ant

.PHONY: Main

all: build

build:
	$(ANT) build

Main:
	$(ANT) Main

clean:
	$(ANT) clean

