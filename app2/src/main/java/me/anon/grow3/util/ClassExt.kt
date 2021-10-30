package me.anon.grow3.util

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import me.anon.grow3.di.ApplicationComponent
import me.anon.grow3.ui.common.Extras

typealias Injector = (ApplicationComponent) -> Unit

public fun codeOf(any: Any): Int = any.toString().toCharArray().sumBy { it.toInt() }
public inline fun <reified T : Any> codeOf(): Int = T::class.java.name.hashCode().and(0xffff)
public inline fun <reified T : Any> nameOf(): String = T::class.java.name
public inline fun <reified T : Fragment> tagOf(bundle: Bundle = bundleOf()): String
	= T::class.java.name + "." + bundle.getString(Extras.EXTRA_DIARY_ID)// + "." + bundle.getString(Extras.EXTRA_CROP_ID)
public fun Any.nameOf(): String = this::class.java.name

public inline fun <I, O> I.transform(crossinline block: I.() -> O): O = block(this)

public fun Any?.toStringOrNull(): String? = this?.toString()?.takeIf { it.isNotBlank() }

public suspend fun <T> tryNull(block: suspend () -> T?): T?
{
	try
	{
		return block()
	}
	catch (e: Exception)
	{
		return null
	}
}

public inline fun <p1, p2> whenNotNull(param1: p1?, param2: p2?, crossinline block: (p1, p2) -> Unit)
{
	param1?.let { p1 ->
		param2?.let { p2 ->
			block(p1, p2)
		}
	}
}

public inline fun <p1, p2, p3> whenNotNull(param1: p1?, param2: p2?, param3: p3?, crossinline block: (p1, p2, p3) -> Unit)
{
	param1?.let { p1 ->
		param2?.let { p2 ->
			param3?.let { p3 ->
				block(p1, p2, p3)
			}
		}
	}
}