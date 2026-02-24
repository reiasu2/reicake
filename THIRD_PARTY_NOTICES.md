# Third-Party Notices

This file lists third-party libraries used by this project, along with their
respective licenses. These notices are provided in compliance with the license
terms of each dependency.

---

## ClassGraph

- **Artifact:** `io.github.classgraph:classgraph:4.8.174`
- **License:** MIT
- **Homepage:** https://github.com/classgraph/classgraph
- **Used by:** `forge-port-api` (runtime classpath scanning)

MIT License

Copyright (c) 2019 Luke Hutchison

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---

## NeoForge

- **Artifact:** `net.neoforged:neoforge:21.1.173`
- **License:** LGPL-2.1-only (NeoForge), Minecraft EULA (Mojang)
- **Homepage:** https://neoforged.net
- **Note:** NeoForge is a modding platform; it is not bundled in the output jars.
  Minecraft is proprietary software owned by Mojang Studios / Microsoft.
  See NeoForge's own LICENSE for authoritative terms.

---

## JUnit 5

- **Artifact:** `org.junit.jupiter:junit-jupiter` (test only)
- **License:** EPL-2.0
- **Homepage:** https://junit.org/junit5/
- **Note:** Test-scoped only; not included in release jars.

---

*This file is informational and does not constitute legal advice.*

*Maintainer note: If any third-party library is shaded/bundled into the
output jar in the future, its license and copyright notice must be added
to this file and included in the jar's `META-INF/` directory.*
