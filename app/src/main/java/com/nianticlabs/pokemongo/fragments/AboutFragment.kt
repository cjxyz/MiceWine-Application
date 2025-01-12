package com.nianticlabs.pokemongo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.nianticlabs.pokemongo.R
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.appRootDir
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.miceWineVersion
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.ratPackagesDir
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.selectedBox64
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.usrDir
import com.nianticlabs.pokemongo.core.EnvVars.getEnv
import com.nianticlabs.pokemongo.core.ShellLoader.runCommandWithOutput
import com.nianticlabs.pokemongo.core.WineWrapper
import com.nianticlabs.pokemongo.databinding.FragmentAboutBinding
import java.io.File

class AboutFragment: Fragment() {
    private var binding: FragmentAboutBinding? = null
    private var rootView: View? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        rootView = binding!!.root

        rootView?.findViewById<TextView>(R.id.ApplicationVersion)?.apply {
            text = "App Version: $miceWineVersion"
        }

        rootView?.findViewById<TextView>(R.id.RootfsVersion)?.apply {
            text = "RootFS Version: ${File("$ratPackagesDir/rootfs-pkg-header").readLines()[2].substringAfter("=").replace("(", "(git-")}"
        }

        rootView?.findViewById<TextView>(R.id.Box64Version)?.apply {
            text = "Box64 Version: ${runCommandWithOutput("$ratPackagesDir/$selectedBox64/files/usr/bin/box64 -v").replace("\n", "")}"
        }

        rootView?.findViewById<TextView>(R.id.WineVersion)?.apply {
            text = "Wine Version: ${WineWrapper.wine("--version", true)}"
        }

        return rootView
    }
}