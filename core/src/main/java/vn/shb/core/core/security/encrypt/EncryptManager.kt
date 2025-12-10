package vn.shb.core.core.security.encrypt

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

class EncryptManager {

    companion object {
        const val TAG = "RSAEncrypt"
        private var publicKey: PublicKey? = null
        private var em: EncryptManager? = null
        fun getInstance(context: Context): EncryptManager {
            if (em == null) {
                em = EncryptManager()
            }
            publicKey = getPublicKey(context)

            return EncryptManager()
        }

        @SuppressLint("NewApi")
        private fun getPublicKey(context: Context): PublicKey? {
            try {
                val inputStream = context.assets.open("public_key.pem")
                val pemBytes = ByteArray(inputStream.available())
                inputStream.read(pemBytes)
                inputStream.close()

                val publicKeyPEM = String(pemBytes, Charsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replace("\n", "")
                    .replace("\r", "")
                    .trim()

                val keyFactory = KeyFactory.getInstance("RSA")
                val keySpec = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyPEM))
                return keyFactory.generatePublic(keySpec)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    fun encryptRSA(plainText: String): ByteArray {
        return try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedData = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            encryptedData
        } catch (e: Exception) {
            e.printStackTrace()
            "".toByteArray()
        }
    }

    @SuppressLint("NewApi")
    fun decodeBase64(encodedString: String): Bitmap? {
        return if (encodedString.isEmpty()) {
            avatarDefault()
        } else {
            try {
                val decodedString = Base64.getDecoder().decode(encodedString)
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            } catch (ex: Exception) {
                avatarDefault()
            } catch (ex: IllegalArgumentException) {
                avatarDefault()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun avatarDefault(): Bitmap? {
        val avatarDefault =
            "iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAYAAABccqhmAAAACXBIWXMAACxLAAAsSwGlPZapAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAACc9SURBVHgB7Z1djBRV2scPxihvhIEbgYSPt5ePTYY4itGEAc3urM5Msr4mgnAhK8nOcLGQ7BsRjXojCuKNmFXZrIm8F8uYqHgBy5C4msyw7rhRYBI2i46ZSeTDfmVIBG/4MsG9Yc+/umqoqj7VXd39nKpz6jy/pO3pmkalu57/eb7Oc6YJxmpu3LgxWz6V/Efw86zQzyL0HP9ZRVnxM54vycdl/+fgdXnatGmXBGMt0wRjPCEjXyEf94iKca8QN40+Tzwh8B9fysdJXJPCMCIY42EBMAzf2FeIm8beJeqv2qYCMSjLx2f+zyfZYzALFoCc8Q1+jbhp7CtEsYEQjAhfFKQglAWTGywAGRNa4R8TFcMviRa48uNP4sq1n8TE2Qve8+SFy/Ladfn4t3x93fv9+QuXp94/GfpZxYK5s7znthm3i5l3TJ+61nbHbfIx3ft5Pl7L31eu3y5a5KT/OCwfI+whZAsLQAb4Rt8nKkYP4284bp+8cMUz8snvL4vxb3+IGHyeQADal8z1npcvniOWL5njCQR+bpIR+XhXVMSgLBitsABowjf6raLi1nc18mdh7KNffSfGpZGPjp3zjB4ruU0EwrB88Z3yMVf+PKcZURgRFc9gkMVADywAhMRW+q60f+74V+fExJkL4rg0dhi+bcaelkAUejuXes+ddy9s5I+PCPYMyGEBIEAafpd8elmkdO+xwg8f/UYMHT/tGX5RDb4eEISVdy8SvauWyeeFU/mHFAzIx2EpBIOCaQkWgCYJufhPixRGj1V++Ng38nE697jdVJA/WNmxUPSs+nla76AsH2+JihiUBdMwLAAN4q/2MPw19d4bGP3B4a+dXeWbBd4AvIJ13R1pxWBAPt7lBqTGYAFIScjN76r1vvEzF8Xw8VNi36ETbPREQAx6Vi0V/WvuTxMmjIiKEAwIpi4sAHWQht8nKoZfSnoPDP3g0JgX0yOJx+gDOYP13XeJdT131XtrWT52shDUhgUggTSGj9X+4JExdvFzIAgRtj75QD2voCxYCBJhAYiRxvAR2+95/wte7Q0BXgGEoE6uoCxYCKpgAfBJE+NjpT9w5Gs2fEOBJwAhqBMelAULwRTOC4A0fNTu3xQJhg/Xft/gCc/4uXxnBymFAD0E21wvHzorAH4dHyv+00nvgeHvee8Lju8tJaUQDIiKR1AWDuKkAEjjRx1/h0ho4MFqjxifV/xikEIIysLRsMApAajn7nNyr9hACLb/7iHRs3pZ0lvK8vErl7wBZwRAGj/c/R2q32Glf+6NT9jwHQGeQJ3y4Q4pAjuFAxReAPxVf59QTNoJEnxF6trDBhsM7sCe/OAGXzC3LfJci8vXfhJX/c8Cm5aCoSJXf7zuDxspTj5k68YHvO7ChKEmZeGAN1BoAai16sPdf/6Nj62M83HDwrjbsc9+8Z1iwTxp7HNmec8EE3pqAgHAfILJi5e95/PyefyMP6DEQnFIkR8otDdQSAGQhl+ST4eEYtWHwe/a+6kYPnZK2IC3ZbZjkTTuNq/hBUM1Gtg2myn4bMfPXhQTZy5WZhxYJAp1woKyKKg3UDgBqJXht6GsB4PHdlis7L0yWWWqsacF7dIQheGjp8TEtxe8sMJU6ngDmFWISsFbokAURgBq1fVNT/J1yhW+e9VS0YkVfknTs/SsYNzzDr7z5iKMjpn5fdTxBiAAO4syvLQQAuC7/H8Xiv59uPrP/eFjo1b9ygDNuZ7Rr+/t0B63mwqE2ZuZIL0DbKE2iTreQFkUJCSwXgCk8f9WVFQ54vLD4OHuw+03hWCld9nokwjEAE1YJnkG/Wvuk9WCB1XfFzyAbbY3D1ktAElZftxMv3nhQyMy/LhxUGrqX3s/G31K8L1BCLDV2oScAbyBD157IikksLpKYKUA+PE+avtVY7lMSfRhtX9q42ovrmeaZ+gYpiv9M3evAOId9A0owMaifhvzAtYJQK14H+W9PF1+3CSIGdf1dLRyMAajAF4BhP3gka9FniAk2L75YdWvysLCvIBVAuB39aG+Xwpfx82xZdchL8OcB+zmZ4cJ4QEqNe9sX6sKCcrCMhGwRgD8gR0w/kiyD0YP488j3mfDz4+8haBGXgBhQL8tZxZYIQB+pn8gfh3u4K53/pZ5vM+Gbw55CgG++xe3PCTWd3eofr3NhqYh4wUgKdOPbbuICbOmRlmIyZE8cwRIDqJnQIHxFQKjBcAk40dWf/ezv7a+Nbfo5CUEtoqAsQKQZPzYwXdgOLsvF1todz/zCJfzLAP3yB/f/zzTsAAVoNflvaLAWBEwUgBUxo84f/MrhzLt54eic5xvN/AG4DFmBc4q2PvS46p7xkgRME4Akoz/Ny/sz6zMB3f/xc0PFX5jjitUOkP3Z+YN4L754LUNVoiAUQJggvFvl4af0O3FWE6W3oAtImCMAORt/PjCdm97hFf9gpOlN2CDCBghAP4Qj0jNNEvj59Kee2TlDdQQgT4pAu+KnMldAKTxY0PPofC1rIwfX8ruZx8RvauWCcY9sqoU1BABtA2PiBzJVQD8jT3/ErH23g0vfKg921+jn5txiKxCAlQH9ksRiIG2YYjASZETt4icCO3qixg/6vy6jR8uPxSZjZ/BPfCPgS3aE7+jX50Tz8l7Owbu/UO+LeRCLh6Av58fK38pfD2LDj/O8jNJZJEXSOgYLMvHvXnME8hLABDzR4Z56DZ+xF/vvLSWO/qYmmBgKRrOrmrcYJYgAoNSANaKjMk8BPDLfRHjR9+2TuNHO+9Hb/ex8TN1wT3yV3mvpDlFqVlwrx84Mha/vMa3jUzJ1AOQf8E+URnlNQWSMI/+fkDbll5O9jHNoDs5CI/0g90bVJOjMi0PZiYAqoy/7uGdNcovDFMXLEobnt8vJs7qKUcnDBVBHuDerKYKZRIC+Em/qoy/zkk+67vvYuNnWgL3zn65Sq/rvkvoIBhlF8OzFd9mtJNVDgCxTSl8AQM8dTX6wPjR4MPGz7QK7qHX5b2kSwRgA7v2/i1+uSQqNqMd7QLgt/lGjuvyjuTWNL03MH6GoUSnCOwb/KfKHp6WtvO00IzWHEBS3K8r6cfGr8Y70lt+7ji1F89XfrwuH/+OvKftjtvkY7qXN5kvY1Iea64Gx8zpmDYET+Mjr/qQbT5AtwB8K0KuP25EGL+OuJ+NP4p35t6xb8T42R+a7qysHEd+p3dacefdCwVTQZcIwPghArHQ9aQUgHuFJrQJgGp7r66DOzjbXwHxJA7Z3HfoBLmHhZsT/ew1Ts11Bp3VgYSDR3Aa8Q6hAS0C4Lv+34avYXSzohe6ZSpNPv1OGz9We3RSZjUurc7x2U6gUwT2vrRWel1VO1Tv1bFpSJcARFx/XfV+GL/Lm3rweT73xieZzkkM47oQ6GoWSsgHaAkFyKsAvutfCl9D6yO18XudVA4bP1Z85FPyMn4Ar+4XfXszHbppEpVGng1iJrH3Ce9C4S2vkLa1QxBD6gFk6fqjg8rF3v68V/0k6hyhXWiwgQgeLjUJO1dJQwFqD+Dv4RfeIQ0aVge4nS4aP2L9vFf9JIIwb/jYKeEauBdfVJ8Y3BIJnvObghAyAfA3+pTC13S4/pX5fQ8I10D1BPFm1ucgNgK+a2ylzfOI9rzYJO9L6jkT+K4VXYJdlA1CJALgu/6R1sXKgY20tVIk/bZrUFrTgReFEqot4P/VxbwAFqZ24gaq4WOnvRAjxstUewWoPIBI4g/KRX0DBBl/18jrENRWyfpEHhNAYholPOqk4PMy5xPz/GD8JHsFWhYAf/XvC1+DC0jt+j/15IPOJZgQT9to/AEJgy8KDe7R14k7UmFLaO6Kgb0CK0SLUHgAkaREcDorJUj6re/RsxHDVLxs/x/oqydZ8+o7n2rb8m0qGDNPnQ9IWFRbTgi2JAB+4i862+89etfftaRf5VyED41O+KWlSH+XRqDOByT0BiAh2CVaoFUPIBKHDMmEBXXiz8m4X0P1JE8qXuHnwiW8OQLP0IYCGC2uSgiKFmhaAFRlv1erSxYt4WKbKTb0FLGMhj3vxw3sX9AJNqlR9wcoEqtdvi02RSseQFXZj3LVctH1B4oRUYXBxdIg+gNWEm6lhhcAW4vRtBfQlAAom36Iv1wXXX9qETWNhJu38Gz/nXYvoNSsF9CsB6B19Xd1h5kLK6SLXgBCAcVBIE3jlQWrw8SmvICGBUD36u+q61/01T8Af0fXcgGgf+39pIeNIFEcq6w05QU04wFoXf3R8OMiB4644xq76AV4R9ETVgVg/IrmoIa9gIYEQPfqjwETrjX8AAioiTv8dIFcgGt9AQC7BikTgoqSe6nRvoBGPYDfhl/oiP1dZPioe1toFauXE1D2BsD2Wq0IpBYAX1m6wteoV39Xp/sMHT8tXOP4mHt5AIB7nLJNWLHXoqHuwEY8gMjqj+EUVKu/l/hzdPWHK+yS+x/gahgAkOSm2jGY0B24NeUfTycAqh1/pKt/d4ezq7+u49FsYPzMBeEiSAhuIvQCFLa4Ju28gLQeQGTDD2XSCqv/OgcTfwETjhoB0HXqrg2gLEjpBYxXf5appgalFYCIS0G548/l1R8cHzsnXGXcYQGg9gIODlflAlKFAXUFQLoSWP1L4WujRDet66s/cDUOBi57P4DSC0A1ID41KE0yMI0H8Fj8P0SV/HN99QfnHRuWESZ+QKlrUHoBMH6FF1C3JFhTAFTJP8qONddXf+DatJwwV65dF65D6QUMVY9kX1EvGVjPA+gKv6BM/rlc92cquBz+BMALWN/TIShQJANh/H21/kw9AYjU/jHUgQpX6/4ME4fSE1Z0lT5W6/2JAuC7/12RfznRqS8rOxbx6s8wPssXzyHbI6DYJtxVKwyo5QFEav+UnX843YepMNPhY81dPtI9DpVHjLBK0RmY2BNQSwBi2X+a+e4o/fWuXiaYCrNmTBeuQrk/3nawU5BqMVB46r9Meq9SAFTuP1XtH6U/5ibUR0nZxMw7/kswN6EqCSp2CCaGAUkeQFf4BaX7z6W/KC7nQpYvuVMwN0FJkIKEMKBP9d4kAYhk/zn5p48Fc9x1g/leiIKcCFUyUGGzympAlQD4rkJX+Bpl7Z+JsvKeRcJVVt7t7t89id5OmvwYThWOoWwKUnkAXeEXcP2pNm10Eo5DKgooAblYCcBqt9zh/EcS63o7SO4H2G0sbIfxVx0mqhKAiKuA7iIKelYtZZcvgU4HV8KVvBgo8YRxCY0wKsKANfELKgGIqMQQUfzfs+rnglGD02Rdg++HZKjCgKEUeYCIAPjlv4gAUM1wZ/c/mZ7Vy5wKA7DKcS9IMggDKJionjZViucB4h5AzPjPiasEGzY4+18byg0hNgDB4y7AZKiqAQnlwEgYEBeArvALqoENvauXCqY2yJG4Am8Eqw9VGKAYuxZZ5OMCEGkZpBpXzeWe+lAfGmEqvA08HVTlYYUHELHxKQHwY4OIOlBMbUW/N5d70uHCysirfzqoysOKKl6kHyDsAUSNX7oOFPG/y73ujQIvYF13cZulePVvDIrqEPIAijb+KVtPFIAJonn1XO5pjO1bHi5kRcDlw1+ahcpzVnkBwQ9hAYjEBlTdf1RNDa6ADPDWjcU7IRmnPvPq3xg9RKXS8bNVofw9wQ9hAShF/1DrAsDtns2xac19hQoFXD31uVUgmDR5gKpEYFfwQ3IOgCAB2L54rmCaA6FAEQZm4O+wffPDgmkOijbxyQtX4pemGoI8AYgfIECVAOTuv+aB9/TBaxusFgH8v+PvwE0/zUMRNiUkAkv4R+ABRNoDz1crRlNwBaA18OXbKgKB8XPc3xpUG8UUh9B6Hv8t4Rc330zTAcgJwNaxUQTY+OloX0wzNen8RXUpMBCAe8K/oUoA8g1Ag00iwMZPC1UiUGHT3hcUCEAp/JsrJA1AnACkJBCBHoO3Dnd2LBIfvd3Pxk/MQoLPU7GvJ+IBlMK/oQgBeOAjPTCsvS+tNbKhZvvmh8QHu5/ghJ8GKHJpioNYS/jHLX45YPbNN/5EUgHgVUAfWzc+IP4xsNmIHgtv1f9Tn+gnPOueiULxPaMKoDg+fPatIrb6U43/nj+HBUAnENiP3u4TB4a/Fn98/3NVrVfzf7/N6+7jBh/9LJg3W1CA05hjHloJAjA7+iaaE1sXzGMByIL1fpddVkKAFf9x7uzLlPlEyV+UAmOeeanKAzhP5AFwCJAtgRBg/zdOhhkd+45MDLBqoJ0XCUgXB5jmDZUtKUJ7PSEAbhhOBuUDDDQwUig+BAG7wVAHTlveXSDDt3b/xFr0crDR5wtsCaXAVnNzqm7A6hCAJAHIhz6aAIwXj02hI6cgCld+vO79HHh7M2dMn+rbaPN/ZswCh8i2KgDB9x5itiIHcF20Ch/6aC7cnWkn86U4t+qdKxb3WegD+O/wlcmLrceNbTNuEwzD0EFxhqRicS/dIjTQ5vCZ9wxjKleuVTUDeZ2ApfCFqwQhwALuAWAYUigqAYoNQdUeAEUSkGEYO9AUAnAWmWEo0dVXUxUC0PQBcA6AYUxD1QegxQNgGMYOWAAYxmFYABjGYVgAGMZhWAAYxmFYABjGYVgAGMZhIADl8AUe5MEw5kGxS1dh22UtHgDVXEGGYSroatHnEIBhHKHtjupt+lUhwHyCEGDyInsADEMJhVetGNRTHQLwOCiGMQ9dIQBGgv1/+AKFAEx+n+2MeqY2wXRgrCJXf7yeOPo98P6wmxMbuni2ozmQzOqcV/V9liEAl8JXKKb5nOcQIFOC898nzlwU42cveMdATeD52k8tu47IHEMYsDDghJrgNWYLsreYHRSDehTf1+VqASD4UilKFowarOajX33nGTp+njh7UWvVBf/u4N8/fOxU5He4V9qXzJXCcKc3OtwTBgOOKysi5/Rs078EASiHr1D0AWBFwoNXiNbAZ4gx3jjZ9fjYOc/wTZrYhP+XUe/cge/EvsF/etfwna/E2QQdCz1x6Lx7oWBaQ+N5nXpCAIBVg1eDxjn+1TnP4IeOn/aebRvRhv9feAqBtxB4Cb2dS8XKexbxPdEEZOd1zlXnAMrhK+2LaY71nvyeBSANwSo/fOwb70ivos1kDHsJAKsQThzqXbXMO2qMqc95qiPeZqQIAag8AE4EJuOtkkdPiQNHvrZylW8FL6cwfNkTO3gHPauXsRjUYfL7S4ICZQgwbdq0Szdu3MB/wTshKDgiqlW3I+05dC4B9/6gNPrho9/w9GVREUIIQVgM1nV3cN4gBslhPdXndV6C7d/qvyjLx4rwm1sFKxtTucn3DZ4Q+w6dYKOvQVgMsABtffIBL1TgzWk0i2n74rnxS2X8IxCAL0VIAHAybKv/Ud3n1JsOVvs9738xFfsy6YH3+dwbH3s/41hy172CcYLFVHFcn9cAGAhAJMhA8u6gaI2gOcU1BWfDpyXwClBaXN99lycILlHp3mzdc1xe7QGcxD9uDb8IWDBvtqAA2W1XBIANXy9BJQGfMcIDV4Rg4uwPgoL26oqcZ/O3hF/cfDNNKXB0rPjGAMPf8MKH4jcv7Gfjz4AgPPhF317vsy8640S5tAXzqhZiz+sPJwFvvlmu2jNlIrBV16PIg0EqN+InbPQ5gc8fogtPAB5BUT1NdIC2SrCPI4ysAIzg+Rb/BdSgHH7DQoIPdLSgCg039NHfD7DxGwDyA/AG8J0UEQoPoD0h/gfheQCfhd+BpEurBInAooCcxqP/OyD2vPcFl/QMA98JhKBQ95usxJEkAJdUhfRTIwDCAhDJA1C18cZ3kNkKavlwOSECjJnA+CEC+K6KwATRvabwAEaCH24NXYwIwEqiuqvtHYFY6V/d+zdxQLqajB3s2vupJwZbNz5o9Y7UIaLFE7MbYihDgGgp0E8Etgp63m0lSDSx8dsHticjT2NzSDBBsHiqEoBCJQB+IjAiAp1EeQAbvYCK8X/ILr/FBN+hjSIwTjToRZUA9G3dIz4UNJIIxFAHCmzLltt84zBRbP0uR7+ksZne1UvjlyI2HheAaEPQEppE4JBFiUA2/uIRfKc2VW4wEIYCRQfgSPhFXAAGwy8QAlDkAdAPYMuHv2XXITb+AoLvdPMrfxE2gP9XCq8Z8b8ijI8s8hEBUDUELSfyAg4OjwnTQTMJx/zFBQvRLlnRMR2qBjpFJQ/xfzl8QXU02OHwi95OmkktpocBUF00kzDFBtWB44bnpKhspWfVz+OXTsYvqAQgEgZgSgsFaGowOQxAjMi4gcltw1iIqJrnFDMUDscvqAQAKjFVJkA/ANWocFPDAPSTc9zvDnCxTfUCqNx/nNWgsNuR+IUqAVD1A1ANbDQ1DMBwTsYtTPUCqO5FxV6ekXD9PyDpePCIq9CzaqmgwMRqAFXGlbEL3IumeX2U96Ji0X5X9b4kARgIv6AqBwIMxzQJm1uVmdYwbaPaQaKWcxzqqij/jajeqxQA31WI/IH1PR2CAtN2alEMXGDsxLQ8wEG97n9Z9d4kDwBEWgapwgCEACZ98Oc5+ecsVPP2KBg+dposJFnXXTUv8XDSe2sJwFvhF5RhgEkJGD7AxF1MOsX6z0SecYL7P5j0/kQBUIUBm9bcLyhAAsYUw1tQfWAi4whtM8yYFaA5+Zfo/oNaHgCIVgNW053fZkpPwO5nHhGMmzz15IPCBCg7UPurF+l3a72/ngAMiFBTEAYLUE0KMuUkXLhL/USeDWMP/Wvuk4nt/M8WwOpPlvzrWJSq+SdMTQFQNQVR7Q3wzswzpCS4deMDHAo4BL7r7ZsfFiZwkLAJTXFYykAt9x/U8wDAzsh/pLeDridg0IwDM7Ft8oPXNpD9vRhzCb5rE/BWf8Lav8KjOVzvz9UVAP8AgakwAB8gVU+ASV4AXKftmx8STLF5Ua78phwigtWfqvSnqP2Xpe0O1vtzaTwAsCf8gvJcNlO8AABhwykzTDHBd2tC3A8oV3+guG/3pPlzaQUg0hNAmQw0yQsAyAdQbX5izAHfKb5bUyBd/dXJv7qrP0glAH4yMPIvpFwpTfICwOvPPqKapcZYCuJjfKemQL76b1wdv1Q3+ReQ1gMAEZcC5TNKL2DPe58LU0CeY+9La7kyUADwHSLpZ9IBIaj7U63+CZ1/76b84+kFwE8GjoSvreumSQYCjGoyaXsmXCrcOCwC9hIYv0knB1PW/YGimWkkOPk3DY14ACBSEkRChdJAcO67SbAI2IuJxg8ou/4SSn+pV3/QkAD4ylIOX6P0Akwc1cQiYB+mGj/ifs2rP0p/A6IBGvUAQMQL6F97P2kDzfNvfGLc1CAWAXsw1fgB5S7YhNV/p2iQhgXAV5hy8BrJFZzCSgViJNOmBgEWAfMx3fgpc1wUqz9oxgMAEaXZtOY+UsPwDugwcJ8+i4C54AAbU42f+swJqtUfNCUAcS8AUG+tNPUEF9xgH73dz81CBoHvwlTjB9RnTlCt/qBZDwBUVQSo+gIAEoKmzQ8MCPoEuG04f/Ad4Lswqc4fhtr1p1z9QdMC4CvOSPgatUHs2vup0SO70FqKgSK8izB7YPCvy8/epPbeODqOm1NsY2569QeteAAgojyU3YEBz7/xsdFHikGN//p2H+cFMgTx/kfyM19nyMaeJKhdf/x9FaFn06s/aEkAVN2BrxOviDit16Q2YRWIPf8xsIUnC2UAJvmYHO8HULv+QOFhD7ay+oNWPQCwLfwCX8wmYkNAm7BphziowDwBhATsDdBTKfE94bnApsb7AWj4oXb9YfwK0dsmWqRlAZAKhJFhkY1CaA6iNoLn/vCxFQd4IiTACqWYzc40CVZ9VF4Um16Mw4v7icfew5ZgUzFS7/irxTRBwI0bN2bLp2/lY3ZwDS291DFQUOs1fQUIwGeAHMbkhSuCaRzc+PCobDD8gF/07SVfqPAZxDL/Zfn4FYUAUIQAwbyAqoQgda3chnxAGHwGyA1UxlBxWJAWr7tUurz47GwyflStqI0fiT9V2Y/C+AGJBxAgPYG/y6eu4DU+jP/5/YC4SpzFR6xtW8ItKAkd5KPIawLDh7tri5cXALefOu5PaG1G2e9ngghqAVghn/4VvvZnmcB7VUNXHxJCNq0OASwEarDSJSS6jOf4V+dkuLtfUKNw/cHPqFZ/QCoAQIrADvn0cvjaBvnhoLOPEqwQH3n1d/tuGMBCUPkOYfjw5mz+Hh+VXi51rwo+l9erT62C679DEEIuAECKALyAFcFrXaFAZXPOE9bePACfzfDRU2Lf4RPOJAth+DB6G139MPjukOimjvuzcP0DdAlAVSgwJOv4W145JKixrTJQiwMYGCEfo2NmDUWhorNjkXhq42orQ7c4WPGx8usoTWfh+gdoEQCgCgWQJdWxwadIIgBwUyGuHJCfle3Hl8Po0R5u+2ofBsaPmB9VKWrQ86Do9yd3/QO0CQCIhwIV1dynxdVNiJmsJxADmzwDGH33qqWid/Uyq8OzJNDbcWCYPncD1x8NTzGh1OL6B+gWgJKohAJTDUK68gGgqCIQAAEdP3PBa4seP/ODMYIQHBSDvo/lS+YWZqVXocv4E5La6K+5V4frH6BVAIAUgafl05vha7pKg6DoIhAH3YZwRSdkqADPauLsBW27J3GT4gZtXzxXPu70Qq+iG3wYXcYPEnpbtknjf0toRLsAACkC+EtsDV/TlQ8ArolAnMBTwPPk95c9bwvicOXa9briEKxAcEexq3PWjNs9g18wb5Yzhq5Cp/EnxP17pPE/LTSTlQAgBEAoUApfRygwoSnJBRGwYecYYzYQTHiruozfm23wp7745bKouP6XhGYyEQCQlA9ANlVX/bto1QEmW3Rm+0FCvV973B+GZDNQGvy/UNXsgHe2P65tpBa+OF21WqbYBIuTLuPHopQw2GRbVsYPMhMA4E8viewaxCqNBIgudHVrMcUluGd0GT/Y7u0QrTL+na1O+GmUTAUA+A0NkaPG1/d0aJ2wG/Rrm3bsGGMe6LnQ7TXiXlfMMxzU1exTi8xyAGGSkoLYHEM9TSUOpsjyOG9GBapSqE7pBPeeYpJxWWSU9IuTiwAAPymI+QGl8HWM/tK9Qy6h7MI4CpJ9WHx0n0OxvvsusfvZqvJ0WRBN92mG3AQA+JuGIAKzw9d1bB+OU4SdhEzrwNXfsuuQ1ngfoD36g91PxC9nmvFXkasAACkCXaIiAlNAkTc8v19bj0AAjD8hHmMcAC3V8Dh1nztRoxy9Vhr/oMiR3AUASBHok0/7wteyEgHAeQG3yMrlBzWMX3ubbxqMEACg2j6cpQhwSOAGcPWff/Nj7S4/qGH8O/PI+KswRgBA3iIA2BsoLllk+QNsMH5glAAAE0SAvYFigdV+1/99KhPL2fSB2GL8wDgBAEkisPmVv2ivDoRhb8BucM9g1ace110LZPvfeXmtFcYPjBQAoBIBkEWfQBiuFNgJOvoqpzJl1wKeUOcHRho/MFYAQJIIZNExGMfmufUuAYN/7o1PMnP3AxI6/ICxxg+MFgBgkggAFgIzybK0F8dW4wfGCwBQjRUDB4bHvKzuVc2NHHFg/BACnADMQpAvQZy/79AJ7Q09cRDno6U8ITzsz3pnXzNYIQBAisAaUWkWirQN6x4qUgsWgvzI0/ABhnlglgUy/jHQ3osOvxFhAdYIAEjaQAQR2PzKoczKhHFYCLIjb8MHMPp3tq9VfddlUTH+k8ISrBIAkCQCQOeg0bRwjkAPqOUfPDLmnY+Ql+GDGjtJyyLHXX3NYp0AAH+eAMKBNfHfYeT4nvc+zzwvEGfl3YvEJnmzYFY+0zwo5yHZO5rzMBfE+0j0JRxLjw09/Xns528VKwUgIKlCkGdeIA6HB41jgpsfJmF4Z4Dxmf5aWC0AwN9JiApBJDmYZ1koCXgFaBbBKTosBlHwfR0cGhNDx0/nvtqHgcu/deODqs4+rPbbbMj018J6AQC18gKY5/7H9z837uhthAa98uGyGISNfuLMBSNW+wAYPLr6etUhXFlYGO+rKIQAAD8vsEPETiACCAngDWTZQtwI8Ax6O5eKlfcs8s7ZKzI47Xj0y++MW+nDoJ9/97O/ThLmPfKxw8Z4X0VhBCDAbxpCXmB2/HemegNhKmfvzfFWnnacvWe5IOCzHj76jRj/9gfv2aRVPk6dRB8MfqcJQzwoKZwAgFohgeneQBzclO1L5spVaaFXf4Y4mBoyBIeTYvw6fsYKb7LBh0FIhvMpEj5b1PXXFsHlj1NIAQhIqhIAG7yBJAJRWL74TrFgzizv4M758sbFzav7GDTvwFEpouflAwePYmWH0eNnW4w9DDL8u595RHTKMCwBq7P89Si0AIBa3oBXbpKlpjw2FenCO8JbCsLMO6Z7P7fNmC5Fom3qd3hdj2AL7eTFK1OvYfBpThe2BXwWcPX7196fJJpY9ftt6uprhsILQEAtb8C2sIBpjTpJPlDoVT+MMwIAankDYOjYafHq3iNWhgVMfWD4T21cXcvdHxGV2n6hV/0wTglAgN88BG+gpPq9zfkBphrE+U89+aBYnzzVqZAZ/jQ4KQDA9wZ2yMdvk97DQmA3dcp6AYWq6zeKswIQ4AsBWonXJL2HhcAusOKv6+6oleADI8Ixd1+F8wIQUC8sACwEZoMY/3Hp5q+vPcB1RFTc/RHBsADESSMEaHTZ895RMTpmZiura6RI7oGyqBj+gGCmYAFIII0QBOVDCAF7BdkC197bZt3TUa9duizY8BNhAahDGiEACA8wrYa9Ar1gte9etVSs7+2o1/VYFmz4dWEBSIkvBKgYdNV6H7wCzCDA0dPsFdAQdO31rF6WZnPUiOAYPzUsAA0ihaBLPvWJGuXDAOQKAq+AxaAxAhcfm3TqxPYBGMu1hw2/MVgAmsQvH6J0iPkDpXrvhxjAK8DZhuM5TS82HWxs6lm9tBGjR+0edfy3XK3jtwoLAAH+mQV41PUKAMIEDLscPnrK8w6KssGmUbDKL18814vpe6V7n3KbMwwdtXt28wlgASDE9wq6RIpcQRh4Bxh7PXzstLe1tqiCAINfKZN4GIOG2QYpV/mAEfk4LB8DvNrTwQKgiVCI8JhoQAwAxABhAsRg/MwPVoqCty1ZruiescvEHcaeNTHIZEQ+PhPs4muDBSADmvUMwkAUJi9eFhMQB/mAIJggDIjb22bcLtqlK9+OASXzZnkG3+TUosC955U+I1gAMsYfXtolKt7BPfKxQrSAN6HHm8ZzvTKlR1Yb8POVa/+eGuxx1XtdEQqISC1g0AHz/QlDGCLSNuM2+fN0r89+pnwNI/eutz6BqCwqBo8s/kk2+mxhAcgZ3zuACHTJxy9Fi4JgAVjhP/OfB9ng84UFwDB8D2GF/4AglIS9olAWlTj+S1ExeF7hDYMFwBL8BqRAHBA6lPzHbJEvMOiyqBj4Zf8ZjzIbu/mwAFiO7zGUREUISqGfZ4mbDUql0B8p1flXlhU/4xnGHRh78MxGbjn/AZODoubbeE6PAAAAAElFTkSuQmCC"
        val default = Base64.getDecoder().decode(avatarDefault)
        return BitmapFactory.decodeByteArray(default, 0, default.size)
    }
}