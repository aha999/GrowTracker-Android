package me.anon.grow3.data.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import me.anon.grow3.data.exceptions.GrowTrackerException
import me.anon.grow3.ui.action.view.*
import me.anon.grow3.ui.logs.view.PhotoLogCard
import me.anon.grow3.ui.logs.view.StageChangeLogCard
import me.anon.grow3.ui.logs.view.TransplantLogCard
import me.anon.grow3.ui.logs.view.WaterLogCard
import me.anon.grow3.util.*
import me.anon.grow3.view.model.Card
import org.threeten.bp.ZonedDateTime
import java.util.*

class LogType<T>(
	val name: String,
	val iconRes: Int = -1,
	val type: Class<out T>,
	val viewType: Class<out LogView<*>>,
	val cardType: Class<out Card<*>>,
	val logConstructor: (Diary) -> T,
	val viewConstructor: (Diary, T) -> LogView<*>,
	val cardConstructor: (Diary, T) -> Card<*>,
) where T : Log

object LogConstants
{
	public val types = hashMapOf<String, LogType<*>>(
		"Water" to LogType(
			"Water",
			-1,
			Water::class.java,
			WaterLogView::class.java,
			WaterLogCard::class.java,
			{ Water() },
			{ a,b -> WaterLogView(a, b) },
			{ a,b -> WaterLogCard(a, b) },
		),
		"Photo" to LogType(
			"Photo",
			-1,
			Photo::class.java,
			PhotoLogView::class.java,
			PhotoLogCard::class.java,
			{ Photo() },
			{ a,b -> PhotoLogView(a, b) },
			{ a,b -> PhotoLogCard(a, b) },
		),
		"StageChange" to LogType(
			"StageChange",
			-1,
			StageChange::class.java,
			StageChangeLogView::class.java,
			StageChangeLogCard::class.java,
			{ StageChange() },
			{ a,b -> StageChangeLogView(a, b) },
			{ a,b -> StageChangeLogCard(a, b) },
		),
		"Transplant" to LogType(
			"Transplant",
			-1,
			Transplant::class.java,
			TransplantLogView::class.java,
			TransplantLogCard::class.java,
			{ Transplant() },
			{ a,b -> TransplantLogView(a, b) },
			{ a,b -> TransplantLogCard(a, b) },
		),
	)
	public val quickMenu get() = arrayOf(
		types["Water"]!!,
		types["Photo"]!!,
		types["Transplant"]!!,
	)
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "action")
abstract class Log(
	open val id: String = UUID.randomUUID().toString(),
	open var date: String = ZonedDateTime.now().asApiString(),
	open var notes: String = "",
	open var cropIds: List<String> = arrayListOf(),
	open var action: String = "Log"
)
{
	public var isDraft = false

	open fun summary(): CharSequence = ""
	open val typeRes: Int = -1
}

public fun <T : Log> T.asView(diary: Diary): LogView<*>
{
	val constructor = LogConstants.types[this.action]?.viewConstructor as? (Diary, T) -> LogView<*>
		?: throw GrowTrackerException.InvalidLog(this)
	return constructor.invoke(diary, this)
}

public fun <T : Log> T.asCard(diary: Diary): Card<*>
{
	val constructor = LogConstants.types[this.action]?.cardConstructor as? (Diary, T) -> Card<*>
		?: throw GrowTrackerException.InvalidLog(this)
	return constructor.invoke(diary, this)
}

data class LogChange(
	var days: Int
) : Delta()

public fun Duo<Log>.difference(): LogChange = LogChange((first.date and second!!.date).dateDifferenceDays())