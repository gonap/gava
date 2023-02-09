# Gava

Gava is a collection of simple, frequently used libraries.

- gava.Common: a collection of frequently used static functions
- gava.Loggable: minimalistic logger interface
- gava.Storable: minimalistic key/value based storage interface


## Todo


### DevLogger

Reuse StringBuilder by creating a simple Object Pool `Pool<T>`


### New: Pool

Create a thread safe pool `Pool<T>`

- Simpler one using `synchronized`.
- PoolItem<T>