play2-wiki-growth-sse
=====================

This is a play2 application that demonstrates streaming and Server Send Events (SSE).

The source is a file containing coordinates of wikipedia articles, produced by [Triposo](http://www.triposo.com/labs/wikigrowth)

This file is parsed line by line, using [Enumerators](http://www.playframework.org/documentation/2.0.2/Enumerators) and [Enumeratees](http://www.playframework.org/documentation/2.0.2/Enumeratees).
Each time a coordinate is found, it is send on the fly to the browser as Server Send Events.

The browser displays this coordinate on a map, showing how wikipedia has spread over the world.

The demo can be view [online on Heroku](http://wiki-growth.herokuapp.com/).
The code is explained on a [blog post](http://yanns.github.io/blog/2012/08/12/handling-data-streams-with-play2-and-server-send-events/).

