/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2019 Caprica Software Limited.
 */

package uk.co.caprica.vlcj.test.minimal;

import org.apache.commons.lang3.StringUtils;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.test.VlcjTest;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

/**
 * An absolute minimum test player.
 */
public class MinimalTestPlayer extends VlcjTest {

    private static final String[] EMBEDDED_MEDIA_PLAYER_ARGS = {
            "--no-snapshot-preview",
            "--intf=dummy"
    };

    private static final String[] MAC_AND_LINUX_OPTIONS = { ":screen-fps=30", "dst=display}" };

    private static final String MAC_DEFAULT_MRL = "avcapture://";
    private static final String LINUX_DEFAULT_MRL = "v4l2:///dev/";
    private static final String WIN_DEFAULT_MRL = "dshow://";

    public static void main(String[] args) throws Exception {
        System.out.println("download nativ libs from there:");
        System.out.println("windows: http://download.videolan.org/pub/videolan/vlc/3.0.8/win64/vlc-3.0.8-win64.7z");
        System.out.println("macos: http://download.videolan.org/pub/videolan/vlc/3.0.8/macosx/vlc-3.0.8.dmg");
        System.out.println("====================================================================");
        System.out.println("");

        System.out.println("execute in ide");
        System.out.println("add vlc libraries into vlclib directory!");
        System.out.println("specify vm argument -Djna.library.path=\"vlclib/mac/lib\" to start from ide");
        System.out.println("specify programm argument like \"0x1450000005ac8511\" to start from ide");
        System.out.println("====================================================================");
        System.out.println("");

        System.out.println("execute without ide");
        System.out.println("add vlc libraries into vlclib directory!");
        System.out.println("navigate into vlcj4-open-capture-device directory");
        System.out.println("execute folowing commands");
        System.out.println("sudo chmod 755 start.sh");
        System.out.println("sudo chmod 755 gradlew");
        System.out.println("./gradlew clean jar");
        System.out.println("nano start.sh, add capture source like \"0x1450000005ac8511\" and save it ");
        System.out.println("./sh start.sh");
        System.out.println("====================================================================");
        System.out.println("");

        final EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent( //
                new MediaPlayerFactory(EMBEDDED_MEDIA_PLAYER_ARGS), null,null,null,null);

        JFrame frame = new JFrame("Test Player");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
            }
        });
        frame.setContentPane(mediaPlayerComponent);
        frame.setVisible(true);

        String mrl = openCaptureDevice(mediaPlayerComponent, args);
        frame.setTitle("Playing: " + mrl);

        Thread.currentThread().join();
    }

    private static String openCaptureDevice(EmbeddedMediaPlayerComponent mediaPlayerComponent, String[] args){
        String mrl = null;
        if(args.length == 0) {
            if(RuntimeUtil.isMac()){
                System.out.println("Specify an MRL to play: like 0x1450000005ac8511");
                System.exit(1);
            }else if(RuntimeUtil.isNix()){
                System.out.println("Specify an MRL to play like video0 or video1");
                System.exit(1);
            }
        }

        if(RuntimeUtil.isWindows()){
            mrl = WIN_DEFAULT_MRL;
            System.out.println("[optional] you can specify a device name like \"Logitech HD Webcam C270\" ");
        }

        String[] options = MAC_AND_LINUX_OPTIONS;
        if(args.length == 1) {
            if(RuntimeUtil.isWindows()){
                options = new String[1];
                options[0] =  ":dshow-vdev=" + args[0];
            }else if(RuntimeUtil.isMac()){
                mrl = MAC_DEFAULT_MRL + args[0];
            }else if(RuntimeUtil.isNix()){
                mrl = LINUX_DEFAULT_MRL + args[0];
            }
        }

        System.out.println("playing mrl: " + mrl);
        System.out.println("with options: " + Arrays.toString(options));

        assert mrl != null;
        mediaPlayerComponent.mediaPlayer().media().play(mrl, options);
        return mrl + " with options: " + Arrays.toString(options);
    }
}
