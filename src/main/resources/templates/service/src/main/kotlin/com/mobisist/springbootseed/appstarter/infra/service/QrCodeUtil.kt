package com.mobisist.springbootseed.appstarter.infra.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.apache.commons.codec.binary.Base64
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

class QrCodeUtil {

    companion object {

        //生成动态二维码
        @JvmStatic
        fun generateQrCode(url: String): String {
            val size = 316
            val fileType = "png"

            try {
                val hintMap = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
                hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8")

                // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
                hintMap.put(EncodeHintType.MARGIN, 1) /* default = 4 */
                hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L)

                val qrCodeWriter = QRCodeWriter()
                val byteMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, size, size, hintMap)
                val width = byteMatrix.width
                val image = BufferedImage(width, width, BufferedImage.TYPE_INT_RGB)
                image.createGraphics()

                val graphics = image.graphics as Graphics2D
                graphics.color = Color.WHITE
                graphics.fillRect(0, 0, width, width)
                graphics.color = Color.BLACK

                for (i in 0..width - 1) {
                    for (j in 0..width - 1) {
                        if (byteMatrix.get(i, j)) {
                            graphics.fillRect(i, j, 1, 1)
                        }
                    }
                }

                val out = ByteArrayOutputStream()
                ImageIO.write(image, fileType, out)
                val bytes = out.toByteArray()

                return Base64.encodeBase64String(bytes)
            } catch (e: WriterException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return ""
        }

    }

}