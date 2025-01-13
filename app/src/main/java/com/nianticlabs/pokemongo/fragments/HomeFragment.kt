package com.nianticlabs.pokemongo.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nianticlabs.pokemongo.R
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.copyFile
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.usrDir
import com.nianticlabs.pokemongo.adapters.AdapterGame
import com.nianticlabs.pokemongo.databinding.FragmentHomeBinding
import java.io.File
import kotlin.math.max

class HomeFragment : Fragment() {
    private var binding: FragmentHomeBinding? = null
    private var rootView: View? = null
    private var layoutManager: GridLayoutManager? = null
    private var preferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        rootView = binding!!.root

        recyclerView = rootView?.findViewById(R.id.recyclerViewGame)
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())!!

        layoutManager = recyclerView?.layoutManager as GridLayoutManager?

        val spanCount = max(1F,requireActivity().resources.displayMetrics.widthPixels / dpToPx(150, requireContext())).toInt()

        recyclerView?.layoutManager = GridLayoutManager(requireContext(), spanCount)
        recyclerView?.addItemDecoration(GridSpacingItemDecoration(spanCount, 20))

        setAdapter(requireActivity(), preferences!!)

        registerForContextMenu(recyclerView!!)

        return rootView
    }

    private fun dpToPx(dp: Int, context: Context): Float {
        val density = context.resources.displayMetrics.density
        return dp * density
    }

    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        }
    }

    companion object {
        private val gson = Gson()
        private val listType = object : TypeToken<MutableList<Array<String>>>() {}.type
        private var recyclerView: RecyclerView? = null
        private val gameList: MutableList<AdapterGame.GameList> = ArrayList()

        fun setAdapter(activity: Activity, preferences: SharedPreferences) {
            recyclerView?.adapter = AdapterGame(gameList, activity)

            gameList.clear()

            addToAdapter(recyclerView!!, activity.getString(R.string.desktop_mode_init), activity.getString(R.string.desktop_mode_init), "")

            for (game in loadGameList(preferences)) {
                val name = game[0]
                val exePath = game[1]
                val icon = game[2]

                addToAdapter(recyclerView!!, exePath, name, icon)
            }
        }

        private fun addToAdapter(recyclerView: RecyclerView, exeFile: String, name: String, icon: String) {
            val pos = recyclerView.adapter?.itemCount!!

            gameList.add(pos, AdapterGame.GameList(File(exeFile), name, icon))

            recyclerView.adapter?.notifyItemInserted(pos)
        }

        private fun loadGameList(preferences: SharedPreferences): MutableList<Array<String>> {
            return gson.fromJson(preferences.getString("gameList", ""), listType) ?: mutableListOf()
        }

        fun saveToGameList(preferences: SharedPreferences, path: String, prettyName: String, icon: String) {
            val editor = preferences.edit()
            val currentList = loadGameList(preferences)
            val game = arrayOf(prettyName, path, icon)
            val gameExists = currentList.any { it[0] == game[0] }
            val index = currentList.count()

            if (gameExists) {
                return
            } else {
                currentList.add(index, game)
            }

            editor.putString("gameList", gson.toJson(currentList))
            editor.apply()

            addToAdapter(recyclerView!!, path, prettyName, icon)

            recyclerView?.adapter?.notifyItemInserted(index + 1)
        }

        fun removeGameFromList(preferences: SharedPreferences, array: Array<String>) {
            val editor = preferences.edit()
            val currentList = loadGameList(preferences)
            val index = currentList.indexOfFirst { it[0] == array[0] && it[1] == array[1] }

            currentList.removeAt(index)

            editor.putString("gameList", gson.toJson(currentList))
            editor.apply()

            gameList.removeAt(index + 1)

            recyclerView?.adapter?.notifyItemRemoved(index + 1)
        }

        fun renameGameFromList(preferences: SharedPreferences, gameArray: Array<String>, newName: String) {
            val editor = preferences.edit()
            val currentList = loadGameList(preferences)
            val index = currentList.indexOfFirst { it[0] == gameArray[0] && it[1] == gameArray[1] }

            currentList[index][0] = newName

            editor.putString("gameList", gson.toJson(currentList))
            editor.apply()

            gameList[index + 1].name = newName

            recyclerView?.adapter?.notifyItemChanged(index + 1)
        }

        fun setIconToGame(context: Context, preferences: SharedPreferences, uri: Uri, gameArray: Array<String>) {
            val editor = preferences.edit()
            val currentList = loadGameList(preferences)
            val index = currentList.indexOfFirst { it[0] == gameArray[0] && it[1] == gameArray[1] }

            createIconCache(context, uri, gameArray[0])

            currentList[index][2] = "$usrDir/icons/${gameArray[0]}-icon"

            editor.putString("gameList", gson.toJson(currentList))
            editor.apply()

            gameList[index + 1].imageGame = "$usrDir/icons/${gameArray[0]}-icon"

            recyclerView?.adapter?.notifyItemChanged(index + 1)
        }

        private fun createIconCache(context: Context, uri: Uri, gameName: String) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = File("$usrDir/icons/$gameName-icon").outputStream()

            copyFile(inputStream!!, outputStream)
        }
    }
}
