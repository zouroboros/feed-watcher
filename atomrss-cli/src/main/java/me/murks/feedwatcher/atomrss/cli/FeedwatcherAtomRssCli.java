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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class FeedwatcherAtomRssCli {
    public static void main(String[] args) throws IOException {
        for (String path: args) {
            AtomRssFileVisitor visitor = new AtomRssFileVisitor();
            Files.walkFileTree(FileSystems.getDefault().getPath(path), visitor);

            System.out.println("Tested " + visitor.getTestedFiles().size() + " files.");
            System.out.println("Parsing "+ visitor.getFailedFiles().size() + "/" + visitor.getTestedFiles().size() + " failed.");
            System.out.println("Parsing "+ visitor.getSucessfullFiles().size() + "/" + visitor.getTestedFiles().size() + " succeeded.");

            visitor.getFailedFiles().stream().map(p -> p.toString())
                    .sorted()
                    .forEach(fileName ->
                    System.err.println("Parsing " + fileName + " failed.")
            );
        }
    }
}