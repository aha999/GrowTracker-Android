package me.anon.grow.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.garden_tracker_view.*
import me.anon.controller.provider.PlantWidgetProvider
import me.anon.grow.MainActivity
import me.anon.grow.R
import me.anon.lib.SnackBar
import me.anon.lib.SnackBarListener
import me.anon.lib.helper.FabAnimator
import me.anon.lib.helper.StatsHelper
import me.anon.lib.manager.GardenManager
import me.anon.lib.manager.PlantManager
import me.anon.model.EmptyAction
import me.anon.model.Garden

class GardenTrackerFragment : Fragment()
{
	protected lateinit var garden: Garden

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		return inflater.inflate(R.layout.garden_tracker_view, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		(activity as MainActivity).toolbarLayout.removeViews(1, (activity as MainActivity).toolbarLayout.childCount - 1)
		(activity as MainActivity).toolbarLayout.addView(LayoutInflater.from(activity).inflate(R.layout.garden_action_buttons_stub, (activity as MainActivity).toolbarLayout, false))

		(activity as MainActivity).toolbarLayout.findViewById<View>(R.id.temp).setOnClickListener {
			val dialogFragment = TemperatureDialogFragment() {
				garden.actions.add(it)
				updateDataReferences()
			}
			dialogFragment.show(childFragmentManager, null)
		}

		setStatistics()
	}

	private fun setStatistics()
	{
		val tempAdditional = arrayOfNulls<String>(3)
		StatsHelper.setTempData(garden, activity!!, temp, tempAdditional)
		min_temp.setText(if (tempAdditional[0] == "100.0") "-" else tempAdditional[0])
		max_temp.setText(if (tempAdditional[1] == "-100.0") "-" else tempAdditional[1])
		ave_temp.setText(tempAdditional[2])
	}

	private fun updateDataReferences()
	{
		setStatistics()

		if (parentFragment is GardenHostFragment)
		{
			GardenManager.getInstance().upsert(garden)
			(parentFragment as GardenHostFragment).garden = garden
		}
	}

	companion object
	{
		@JvmStatic
		fun newInstance(garden: Garden): GardenTrackerFragment
		{
			val fragment = GardenTrackerFragment()
			fragment.garden = garden

			return fragment
		}
	}
}
