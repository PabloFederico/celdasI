#/bin/bash
pkill chrome
rm -R "/home/damian/.config/google-chrome/Default/Application Cache"
cp -R "/home/damian/.config/google-chrome/Default/Application Cache2" "/home/damian/.config/google-chrome/Default/Application Cache"
google-chrome --app= http://chrome.angrybirds.com
