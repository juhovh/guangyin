# `Guangyin` <a href="http://travis-ci.org/#!/juhovh/guangyin/builds"><img src="https://secure.travis-ci.org/juhovh/guangyin.png" /></a>

**Clojure date and time library wrapping Java 8 java.time package.**

[![Clojars Project](http://clojars.org/guangyin/latest-version.svg)](http://clojars.org/guangyin)

The Chinese have a saying “一寸光阴一寸金，寸金难买寸光阴”, which approximately
translates to "time is money, but money can't buy time". Whenever working with
date and time handling it is important to have respect for time itself. Hence
the old Chinese name for time, Guangyin.

## Disclaimer

This library is very much work in progress. It is not recommended to use it
in a project before this disclaimer is removed.

## Design Principles

Guangyin is an opinionated date and time library, which means there are several
rules that are used in designing the API:

1. Be liberal in input, conservative in output
2. Enforce use of correct abstraction for the data
3. Extensibility and readability over performance
4. Easy access for lower level methods for performance
5. Always use explicit time zones over implicit time zones
6. Overload existing Clojure functions whenever possible
7. Make error messages understandable whenever possible

This means that input types should always be coerced to the requested output if
it is possible. It should not be easy or useful to use a date time object for
describing a date only, there is a local date for a reason. Extensibility and
readability is the main focus of this library, if performance is needed it
should be achieved by calling the Java methods directly in performance critical
sections.

Time zones are the single most difficult thing in handling date and time
calculations, therefore the programmer should always state in which time zone
they are working on, also it should never be assumed that e.g. a day is 24 hours
in the API. Should never introduce a new function if it is possible and makes
any sense to overload an existing Clojure function. Proper error messages can
save lots of time in debugging, so should always aim for that.

## License

```
The MIT License (MIT)

Copyright (c) 2015 Juho Vähä-Herttua

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
