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

package com.vagell.kv4pht.logging;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BufferedLogger extends Timber.Tree {

    private static final BufferedLogger singleton = new BufferedLogger();
    private int maxLines = 2000;
    
    public static BufferedLogger getSingleton() {
        return singleton;
    }

    private final MutableLiveData<List<String>> allLogs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> newLogLine = new MutableLiveData<>();

    public LiveData<List<String>> getAllLogs() {
        return allLogs;
    }

    public LiveData<String> getNewLogLine() {
        return newLogLine;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        String logLine = (tag != null ? tag + ": " : "") + message;

        List<String> currentLogs = new ArrayList<>(allLogs.getValue());
        currentLogs.add(logLine);
        if (currentLogs.size() > maxLines) {
            currentLogs.remove(0);
        }
        // must use post for logging from background threads
        allLogs.postValue(currentLogs);
        newLogLine.postValue(logLine);
    }   
}
