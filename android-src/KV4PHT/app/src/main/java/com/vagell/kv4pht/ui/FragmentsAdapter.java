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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import timber.log.Timber;

import com.vagell.kv4pht.ui.MainActivity;
import com.vagell.kv4pht.ui.ChatFragment;
import com.vagell.kv4pht.ui.LogFragment;
import com.vagell.kv4pht.ui.ChatFragment;
import com.vagell.kv4pht.R;

public class FragmentsAdapter extends FragmentStateAdapter {

    public FragmentsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new VoiceFragment();
            case 1:
                return new ChatFragment();
            case 2:
                return new LogFragment();
            default:
                return new ChatFragment(); // fallback
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of fragments/pages
    }
}
