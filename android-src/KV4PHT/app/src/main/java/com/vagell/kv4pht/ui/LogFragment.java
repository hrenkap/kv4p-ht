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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import timber.log.Timber;

import com.vagell.kv4pht.logging.BufferedLogger;
import com.vagell.kv4pht.ui.MainActivity;
import com.vagell.kv4pht.R;

public class LogFragment extends Fragment implements BufferedLogger.LogListener {

    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BufferedLogger.getSingleton().addListener(this);

        textView = (TextView) view.findViewById(R.id.logTextView);

        textView.setText("");

        Timber.d("Log fragment created");
    }

    public void onNewLog(String line) {
        textView.setText(textView.getText()+line+"\n");
    }
}
