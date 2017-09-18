package com.mobisist.springbootseed.appstarter.infra.web

import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer
import com.fasterxml.jackson.databind.util.EnumResolver
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Deserializer class that can deserialize instances of specified Enum class
 * from case insensitive Strings.
 */
class EnumCaseInsensitiveDeserializer(res: EnumResolver) : EnumDeserializer(res) {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Any {
        return super.deserialize(TextCaseInsensitiveJsonParser(p!!), ctxt)
    }

    private class TextCaseInsensitiveJsonParser(private val delegate: JsonParser) : JsonParser() {

        override fun getBigIntegerValue(): BigInteger = delegate.bigIntegerValue
        override fun version(): Version = delegate.version()
        override fun getCurrentTokenId(): Int = delegate.currentTokenId

        // default to upper case
        override fun getText(): String = delegate.text.toUpperCase()

        override fun getIntValue(): Int = delegate.intValue
        override fun close() = delegate.close()
        override fun isClosed(): Boolean = delegate.isClosed
        override fun getNumberValue(): Number = delegate.numberValue
        override fun getLastClearedToken(): JsonToken = delegate.lastClearedToken
        override fun getBinaryValue(bv: Base64Variant?): ByteArray = delegate.getBinaryValue(bv)

        override fun setCodec(c: ObjectCodec?) {
            delegate.codec = c
        }

        override fun getDoubleValue(): Double = delegate.doubleValue
        override fun getParsingContext(): JsonStreamContext = delegate.parsingContext
        override fun getCurrentLocation(): JsonLocation = delegate.currentLocation
        override fun nextValue(): JsonToken = delegate.nextValue()
        override fun getNumberType(): NumberType = delegate.numberType
        override fun getDecimalValue(): BigDecimal = delegate.decimalValue
        override fun hasCurrentToken(): Boolean = delegate.hasCurrentToken()
        override fun getTextLength(): Int = delegate.textLength
        override fun getCurrentToken(): JsonToken = delegate.currentToken
        override fun hasTokenId(id: Int): Boolean = delegate.hasTokenId(id)
        override fun hasToken(t: JsonToken?): Boolean = delegate.hasToken(t)
        override fun getTextOffset(): Int = delegate.textOffset
        override fun getLongValue(): Long = delegate.longValue
        override fun overrideCurrentName(name: String?) = delegate.overrideCurrentName(name)
        override fun getCurrentName(): String = delegate.currentName
        override fun hasTextCharacters(): Boolean = delegate.hasTextCharacters()
        override fun getTextCharacters(): CharArray = delegate.textCharacters
        override fun nextToken(): JsonToken = delegate.nextToken()
        override fun getTokenLocation(): JsonLocation = delegate.tokenLocation
        override fun getCodec(): ObjectCodec = delegate.codec
        override fun getFloatValue(): Float = delegate.floatValue
        override fun clearCurrentToken() = delegate.clearCurrentToken()
        override fun skipChildren(): JsonParser = delegate.skipChildren()
        override fun getValueAsString(def: String?): String = delegate.valueAsString

    }

}

