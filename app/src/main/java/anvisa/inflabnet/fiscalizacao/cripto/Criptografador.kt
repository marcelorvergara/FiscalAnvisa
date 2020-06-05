package anvisa.inflabnet.fiscalizacao.cripto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class Criptografador {
    val ks: KeyStore =
        KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getSecretKey(): SecretKey? {
        var chave: SecretKey?
        if(ks.containsAlias("chaveCripto")) {
            val entrada = ks.getEntry("chaveCripto", null) as?
                    KeyStore.SecretKeyEntry
            chave = entrada?.secretKey
        } else {
            val builder = KeyGenParameterSpec.Builder("chaveCripto",
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT)
            val keySpec = builder.setKeySize(256)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
            val kg = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            kg.init(keySpec)
            chave = kg.generateKey()
        }
        return chave
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun cipher(original: String): ByteArray {
        var chave = getSecretKey()
        return cipher(original,chave)
    }

    fun cipher(original: String, chave: SecretKey?): ByteArray {
        if (chave != null) {
            Cipher.getInstance("AES/CBC/PKCS7Padding").run {
                init(Cipher.ENCRYPT_MODE,chave)
                var valorCripto = doFinal(original.toByteArray())
                var ivCripto = ByteArray(16)
                iv.copyInto(ivCripto,0,0,16)
                return ivCripto + valorCripto
            }
        } else return byteArrayOf()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun decipher(cripto: ByteArray): String{
        var chave = getSecretKey()
        return decipher(cripto,chave)
    }

    fun decipher(cripto: ByteArray, chave: SecretKey?): String{
        if (chave != null) {
            Cipher.getInstance("AES/CBC/PKCS7Padding").run {
                var ivCripto = ByteArray(16)
                var valorCripto = ByteArray(cripto.size-16)
                cripto.copyInto(ivCripto,0,0,16)
                cripto.copyInto(valorCripto,0,16,cripto.size)
                val ivParams = IvParameterSpec(ivCripto)
                init(Cipher.DECRYPT_MODE,chave,ivParams)
                return String(doFinal(valorCripto))
            }
        } else return ""
    }

    fun getHash(texto: String): String{
        var md = MessageDigest.getInstance("MD5")
        return Base64.encodeToString(
            md.digest(texto.toByteArray()), Base64.DEFAULT).trimEnd()
    }
}