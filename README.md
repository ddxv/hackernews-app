# An Android App and Widget for viewing Hacker News

This app and related code is free to use for anyone. This was a side project to learn Android, kotlin and the many libraries that came along with it.

There are many Hacker News apps, but this one focused on two features:

1. Widgets: This app supports dynamic widgets using Compose Glance. The widgets can be themed and set up with one of the three main categories: "New", "Top" and "Best".
2. Themes: Both the widgets and the main app support a number of color schemes based on user preference.

## Live in Production

The app can be downloaded from Google Play:
<https://play.google.com/store/apps/details?id=com.thirdgate.hackernews>

## Setup

1. Clone repo and open with Android Studio.  Ensure gradle and libraries build properly.
2. App is built to use an API which scrapes and preprocesses hackernews. The code for that can be found: <https://github.com/ddxv/hackernews-api>
