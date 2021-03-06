package org.sitoolkit.wt.app.compareevidence;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.sitoolkit.wt.domain.evidence.ElementPosition;
import org.sitoolkit.wt.domain.evidence.EvidenceDir;
import org.sitoolkit.wt.domain.evidence.MaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaskScreenshotGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MaskScreenshotGenerator.class);

    /**
     * 指定されたエビデンスディレクトリ以下のスクリーンショットに対してマスク処理を行います。
     * マスク処理に使用するマスク情報ファイルは以下の優先度で有効になります。
     *
     * <ol>
     * <li>基準エビデンスルート/${browser}/mask/*.json
     * <li>${user.home}/Downloads/*.json
     * </ol>
     *
     * マスク処理に使用したマスク情報ファイルは基準エビデンスディレクトリ以下に移動します。
     * マスク処理後のスクリーンショットファイルは、処理前のファイルと同じディレクトリに保存し、ファイル名の先頭に"mask_"を付与します。
     *
     * @param targetDir
     *            マスク処理を行うエビデンスのディレクトリ
     * @return マスク後の画像ファイル
     */
    public List<File> generate(EvidenceDir targetDir) {

        LOG.info("スクリーンショットのマスク処理を行います ", targetDir.getDir());

        List<File> maskedFiles = new ArrayList<>();

        if (!(targetDir.exists())) {
            LOG.error("エビデンスがありません");
            return maskedFiles;
        }

        MaskInfo maskInfo = MaskInfo.load(targetDir);

        for (File evidence : targetDir.getEvidenceFiles()) {

            for (File screenshot : targetDir.getScreenshots(evidence.getName())) {

                if (maskInfo.getMaskInfoAsMap().get(screenshot.getName()) == null) {
                    continue;
                }

                maskedFiles.add(mask(screenshot, maskInfo));
            }

        }

        return maskedFiles;
    }

    /**
     * 画像ファイルをマスク情報に従ってマスクします。
     *
     * @param imgFile
     *            マスク対象の画像ファイル
     * @param maskInfo
     *            マスク情報
     * @return マスク後の画像ファイル
     *
     */
    public File mask(File imgFile, MaskInfo maskInfo) {

        File maskedImg = new File(imgFile.getParent(), EvidenceDir.toMaskSsName(imgFile.getName()));

        try {
            BufferedImage bi = ImageIO.read(imgFile);
            Graphics2D g2d = bi.createGraphics();
            g2d.setPaint(Color.BLACK);

            List<ElementPosition> posStyles = maskInfo.getMaskInfoAsMap().get(imgFile.getName());

            for (ElementPosition elementPosition : posStyles) {

                int posX = elementPosition.getX();
                int posY = elementPosition.getY();
                int width = elementPosition.getW();
                int height = elementPosition.getH();
                g2d.fillRect(posX, posY, width, height);

                ImageIO.write(bi, "png", maskedImg);

            }

            LOG.info("マスク済み画像ファイルを生成しました {}", maskedImg.getPath());

        } catch (IOException e) {
            LOG.error("画像ファイルのマスク処理で例外が発生しました", e);
        }

        return maskedImg;
    }

}
