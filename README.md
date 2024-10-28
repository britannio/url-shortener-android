# URL Shortener app

A native Android front-end for britann.io, my personal url shortener powered by short.io.

All features were implemented via [Aider](https://aider.chat) in ~3hrs at a cost of $3.43
in Anthropic credits.

<img src="https://raw.githubusercontent.com/britannio/url-shortener-android/main/public/screenshot.png" width="200" alt="App Screenshot"/>

## Features

*Must*
- [x] Setup basic UI
- [x] Connect to short.io API
  - [x] Shorten a url
  - [x] Get all shortened urls

*Should*
- [x] Allow other apps to share a url to the app
- [x] Automatically refresh the list of shortened urls when a url is shortened
- [x] Show a loading indicator when any network request is in progress
- [x] Tapping on a shortened url should copy it to the clipboard
- [x] Use britann.io as the app bar title
- [x] If the clipboard contains a url when the app is launched, use it to prefill the text field (this isn't working)
- [x] The app bar color should match the status bar
