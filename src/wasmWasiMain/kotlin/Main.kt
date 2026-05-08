@file:OptIn(kotlin.wasm.ExperimentalWasmInterop::class)

import kotlin.wasm.WasmImport
import kotlin.wasm.unsafe.UnsafeWasmMemoryApi
import kotlin.wasm.unsafe.withScopedMemoryAllocator

private const val PREFIX = "Wasm received:"

@WasmImport("wasi_snapshot_preview1", "fd_read")
private external fun wasiFdRead(fd: Int, iovsPtr: Int, iovsLen: Int, nreadPtr: Int): Int

@OptIn(UnsafeWasmMemoryApi::class)
private fun readByteFromStdin(): Int {
    var result = -1

    withScopedMemoryAllocator { allocator ->
        val buffer = allocator.allocate(1)
        val iovec = allocator.allocate(8)
        val nreadPtr = allocator.allocate(4)

        (iovec + 0).storeInt(buffer.address.toInt())
        (iovec + 4).storeInt(1)

        val errno = wasiFdRead(0, iovec.address.toInt(), 1, nreadPtr.address.toInt())
        if (errno == 0 && nreadPtr.loadInt() > 0) {
            result = buffer.loadByte().toInt() and 0xFF
        }
    }

    return result
}

private fun readLineFromStdin(): String? {
    val builder = StringBuilder()

    while (true) {
        when (val nextByte = readByteFromStdin()) {
            -1 -> return if (builder.isEmpty()) null else builder.toString()
            '\n'.code -> {
                if (builder.isNotEmpty() && builder.last() == '\r') {
                    builder.deleteAt(builder.length - 1)
                }
                return builder.toString()
            }
            else -> builder.append(nextByte.toChar())
        }
    }
}

fun main() {
    while (true) {
        val line = readLineFromStdin() ?: break
        println("$PREFIX $line")
    }
}
