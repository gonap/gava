# Gava

<https://gonn.org> [++]  

Copyright (c) 2023 Gon Yi. All rights reserved.  
__DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.__

Gava is a collection of simple but frequently used code.

- `gava.Common`:   a collection of frequently used static functions
- `gava.Box`:      a null-safe object wrapper.
- `gava.Loggable`: minimalistic logger interface
- `gava.Storable`: minimalistic key/value based storage interface
- `gava.Fn*`:      frequently used functional interfaces. (`T` represents input, `R` represents output)

## Changes

- `v0.0.3`: Simplify `Storable`. Deleted `close()` and `update()` from the interface
- `v0.0.4`: for cleaning up the `Box` 
- `v0.0.5`: for `Box`, added `validate()` method that can throw an exception
- `v0.0.7`: Lambda functions `FnAA` changed to `FxNN`. 
- `v0.0.9`: Add functional interface FxModify and FxThrow which both are based on Fx11.
- `v0.0.10` 
  - Box is no longer autocloseable.
  - Box.toString() returns only its internal value. 
  - FxModify is now called FxUnary
  - FxInt has added
- `v0.0.12`
  - Storable.set() now returns boolean
