play2-wiki-growth-sse
=====================

This is a play2 application that demonstrates streaming and Server Send Events (SSE).

The source is a file containing coordinates of wikipedia articles, produced by [Triposo](http://www.triposo.com/labs/wikigrowth)

This file is parsed line by line, using [Enumerators](http://www.playframework.org/documentation/2.0.2/Enumerators) and [Enumeratees](http://www.playframework.org/documentation/2.0.2/Enumeratees).
Each time a coordinate is found, it is send on the fly to the browser as Server Send Events.

The browser displays this coordinate on a map, showing how wikipedia has spread over the world.

Yann
@simon_yann
