package me.anon.grow3.ui.logs.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import me.anon.grow3.data.exceptions.GrowTrackerException.InvalidLog
import me.anon.grow3.data.model.StageChange
import me.anon.grow3.data.model.Water
import me.anon.grow3.data.model.logCard
import me.anon.grow3.ui.base.CardListFragment
import me.anon.grow3.ui.common.Extras
import me.anon.grow3.ui.logs.view.LogDateSeparator
import me.anon.grow3.ui.logs.view.StageChangeLogCard
import me.anon.grow3.ui.logs.viewmodel.LogListViewModel
import me.anon.grow3.util.Injector
import me.anon.grow3.util.ViewModelProvider
import me.anon.grow3.util.asDate
import me.anon.grow3.util.asDisplayString
import javax.inject.Inject

class LogListFragment : CardListFragment()
{
	override val injector: Injector = { it.inject(this) }

	@Inject internal lateinit var viewModelFactory: LogListViewModel.Factory
	private val viewModel: LogListViewModel by viewModels { ViewModelProvider(viewModelFactory, this) }

	override fun bindArguments(bundle: Bundle?)
	{
		super.bindArguments(bundle)
		val cropIds = ArrayList(bundle?.getStringArray(Extras.EXTRA_CROP_IDS)?.asList() ?: arrayListOf())
		bundle?.getString(Extras.EXTRA_DIARY_ID)?.let { viewModel.load(it, cropIds) }
	}

	override fun bindVm()
	{
		viewModel.data.observe(viewLifecycleOwner) { data ->
			if (data !is LogListViewModel.ViewData.Complete) return@observe
			val diary = data.diary
			val logs = data.logs
			val crop = data.crops?.firstOrNull()

			val title = crop?.name ?: diary.name
			requireActivity().title = "$title logs"

			viewAdapter.newStack {
				val group =
					logs.groupBy { log ->
						log.date.asDate()
					}
					.toSortedMap(Comparator { o1, o2 ->
						-o1.compareTo(o2)
					})

				group.forEach { (date, logs) ->
					add(LogDateSeparator(date.asDisplayString()))

					logs.reversed()
						.forEach { log ->
							add(when (log)
							{
//								is Environment -> EnvironmentLogCard(diary, log)
//								is Harvest -> HarvestLogCard(diary, log)
//								is Maintenance -> MaintenanceLogCard(diary, log)
//								is Pesticide -> PesticideLogCard(diary, log)
//								is Photo -> PhotoLogCard(diary, log)
//								is Transplant -> TransplantLogCard(diary, log)
								is StageChange -> StageChangeLogCard(diary, log)
								is Water -> log.logCard(diary, log)
								else -> throw InvalidLog(log)
							})
						}
				}
			}
		}
	}
}
