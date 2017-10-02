package com.korotyx.virtualentity.security

import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object IntegrityChecker
{
    /**
     * Verifies text's SHA1 checksum
     * @param input text
     * @return true if the expeceted SHA1 checksum matches the file's SHA1 checksum; false otherwise.
     * @throws NoSuchAlgorithmException
     */
    @Throws(NoSuchAlgorithmException::class)
    fun sha1(input: String): String
    {
        val mDigest = MessageDigest.getInstance("SHA1")
        val result = mDigest.digest(input.toByteArray())
        val sb = StringBuffer()
        for (i in result.indices)
        {
            sb.append(Integer.toString((result[i] and 0xff.toByte()) + 0x100, 16).substring(1))
        }
        return sb.toString()
    }

    /**
     * Verifies file's SHA1 checksum
     * @param file Filepath and name of a file that is to be verified
     * @param testChecksum the expected checksum
     * @return true if the expeceted SHA1 checksum matches the file's SHA1 checksum; false otherwise.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Throws(NoSuchAlgorithmException::class, IOException::class)
    fun verifyChecksum(file: String, testChecksum: String): Boolean
    {
        val sha1 = MessageDigest.getInstance("SHA1")
        val fis = FileInputStream(file)

        val data = ByteArray(1024)
        var read = fis.read(data)

        while (read != -1)
        {
            sha1.update(data, 0, read)
            read = fis.read(data)
        }

        fis.close()
        val hashBytes = sha1.digest()
        val sb = StringBuffer()
        for (i in hashBytes.indices)
        {
            sb.append(Integer.toString((hashBytes[i] and 0xff.toByte()) + 0x100, 16).substring(1))
        }
        val fileHash = sb.toString()
        return fileHash == testChecksum
    }
}
