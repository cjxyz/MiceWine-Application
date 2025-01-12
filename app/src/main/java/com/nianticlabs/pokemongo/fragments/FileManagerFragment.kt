package com.nianticlabs.pokemongo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.nianticlabs.pokemongo.R
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.fileManagerCwd
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.fileManagerDefaultDir
import com.nianticlabs.pokemongo.adapters.AdapterFiles
import com.nianticlabs.pokemongo.databinding.FragmentFileManagerBinding
import java.io.File

class FileManagerFragment: Fragment() {
    private var binding: FragmentFileManagerBinding? = null
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileManagerBinding.inflate(inflater, container, false)
        rootView = binding!!.root

        recyclerView = rootView?.findViewById(R.id.recyclerViewFiles)
        recyclerView?.adapter = AdapterFiles(fileList, requireContext(), false)

        refreshFiles()
        registerForContextMenu(recyclerView!!)

        return rootView
    }

    companion object {
        private var recyclerView: RecyclerView? = null
        private val fileList: MutableList<AdapterFiles.FileList> = ArrayList()

        fun refreshFiles() {
            recyclerView?.adapter?.notifyItemRangeRemoved(0, fileList.count())

            fileList.clear()

            if (fileManagerCwd != fileManagerDefaultDir) {
                addToAdapter(File(".."))
            }

            File(fileManagerCwd).listFiles()?.sorted()?.forEach {
                if (it.isDirectory) {
                    addToAdapter(it)
                }
            }

            File(fileManagerCwd).listFiles()?.sorted()?.forEach {
                if (it.isFile) {
                    addToAdapter(it)
                }
            }

            recyclerView?.adapter?.notifyItemRangeInserted(0, fileList.count())
        }

        fun deleteFile(filePath: String) {
            val index = fileList.indexOfFirst { it.file.path == filePath }

            if (File(filePath).delete()) {
                fileList.removeAt(index)

                recyclerView?.adapter?.notifyItemRemoved(index)
            }
        }

        private fun addToAdapter(file: File) {
            fileList.add(AdapterFiles.FileList(file))
        }
    }
}