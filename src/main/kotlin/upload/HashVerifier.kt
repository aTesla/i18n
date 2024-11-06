package org.example.upload

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest

/*
https://developer.android.com/privacy-and-security/security-best-practices
Check validity of data
If your app uses data from external storage, make sure that the contents of the data haven't been corrupted or modified. Include logic to handle files that are no longer in a stable format.

The following code snippet includes an example of a hash verifier:
 */
class HashVerifier {
    suspend operator fun invoke(stream: InputStream, expectedHash: String) {
        val hash = calculateHash(stream)
// Store "expectedHash" in a secure location.
        if (hash == expectedHash) {
            // Work with the content.
        }
    }

    // Calculating the hash code can take quite a bit of time, so it shouldn't
// be done on the main thread.
    suspend fun calculateHash(stream: InputStream): String {
        return withContext(Dispatchers.IO) {
            val digest = MessageDigest.getInstance("SHA-512")
            val digestStream = DigestInputStream(stream, digest)
            while (digestStream.read() != -1) {
                // The DigestInputStream does the work; nothing for us to do.
            }
            digest.digest().joinToString(":") { "%02x".format(it) }
        }
    }
}