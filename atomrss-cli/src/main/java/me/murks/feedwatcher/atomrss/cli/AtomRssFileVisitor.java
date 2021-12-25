/*
This file is part of FeedWatcher.

FeedWatcher is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FeedWatcher is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2021 Zouroboros
 */
package me.murks.feedwatcher.atomrss.cli;

import me.murks.feedwatcher.atomrss.FeedItem;

import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import me.murks.feedwatcher.atomrss.FeedParser;

public class AtomRssFileVisitor extends SimpleFileVisitor<Path> {
    private final Collection<Path> testedFiles;
    private final Collection<Path> sucessfullFiles;
    private final Collection<Path> failedFiles;

    public AtomRssFileVisitor() {
        testedFiles = new LinkedList<>();
        sucessfullFiles = new LinkedList<>();
        failedFiles = new LinkedList<>();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.toString().endsWith(".xml") || file.toString().endsWith(".rss")
                || file.toString().endsWith(".atom")) {
            testedFiles.add(file);
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                FeedParser feedParser = new FeedParser(new FileInputStream(file.toFile()), parser,
                        new KXmlSerializer());
                checkParser(feedParser);
                checkEntries(feedParser);
                sucessfullFiles.add(file);
            } catch (Exception e) {
                failedFiles.add(file);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    private void checkParser(FeedParser parser) {
        parser.getName();
        parser.getDescription();
        parser.getIconUrl();
    }

    private void checkEntries(FeedParser parser) {
        for (FeedItem item : parser.items(new Date(0))) {
            item.getTitle();
            item.getDescription();
            item.getDate();
            item.getLink();
        }
    }

    public Collection<Path> getTestedFiles() {
        return testedFiles;
    }

    public Collection<Path> getFailedFiles() {
        return failedFiles;
    }

    public Collection<Path> getSucessfullFiles() {
        return sucessfullFiles;
    }
}
