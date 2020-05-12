package me.anon.grow3.data.model

import com.squareup.moshi.JsonClass

/**
 * // TODO: Add class description
 */
@JsonClass(generateAdapter = true)
class Pesticide(
	public val type: PesticideType,
	public var name: String = "",
	public var amount: Double?
) : Log(action = "Pesticide")
