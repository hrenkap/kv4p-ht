/*
kv4p HT (see http://kv4p.com)
Copyright (C) 2025 Peter Hrenka

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.vagell.kv4pht.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import timber.log.Timber;

import com.vagell.kv4pht.ui.MainActivity;
import com.vagell.kv4pht.R;

public class VoiceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Timber.d("Voice fragment created");

        MainActivity mainAct = (MainActivity) requireActivity();

        RecyclerView memoriesView = view.findViewById(R.id.memoriesList2);
        memoriesView.setLayoutManager(new LinearLayoutManager(getContext()));
        memoriesView.setAdapter(mainAct.getMemoriesAdapter());

        AppCompatButton scanButton = view.findViewById(R.id.scanButton);

        MainViewModel viewModel = new ViewModelProvider(mainAct).get(MainViewModel.class);
        viewModel.getScanButtonText().observe(getViewLifecycleOwner(), text -> {
            scanButton.setText(text);
        });

    }
}
