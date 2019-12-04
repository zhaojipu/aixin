/*
 * Copyright 2011 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zds.base.code.maxicode;

import com.zds.base.code.BarcodeFormat;
import com.zds.base.code.BinaryBitmap;
import com.zds.base.code.ChecksumException;
import com.zds.base.code.DecodeHintType;
import com.zds.base.code.FormatException;
import com.zds.base.code.NotFoundException;
import com.zds.base.code.Reader;
import com.zds.base.code.Result;
import com.zds.base.code.ResultMetadataType;
import com.zds.base.code.ResultPoint;
import com.zds.base.code.common.BitMatrix;
import com.zds.base.code.common.DecoderResult;
import com.zds.base.code.maxicode.decoder.Decoder;

import java.util.Map;

/**
 * This implementation can detect and decode a MaxiCode in an image.
 */
public final class MaxiCodeReader implements Reader {

  private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
  private static final int MATRIX_WIDTH = 30;
  private static final int MATRIX_HEIGHT = 33;

  private final Decoder decoder = new Decoder();

  /*
  Decoder getDecoder() {
    return decoder;
  }
   */

  /**
   * Locates and decodes a MaxiCode in an image.
   *
   * @return a String representing the content encoded by the MaxiCode
   * @throws NotFoundException if a MaxiCode cannot be found
   * @throws FormatException if a MaxiCode cannot be decoded
   * @throws ChecksumException if error correction fails
   */
  @Override
  public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
    return decode(image, null);
  }

  @Override
  public Result decode(BinaryBitmap image, Map<DecodeHintType,?> hints)
      throws NotFoundException, ChecksumException, FormatException {
    DecoderResult decoderResult;
    if (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
      BitMatrix bits = extractPureBits(image.getBlackMatrix());
      decoderResult = decoder.decode(bits, hints);
    } else {
      throw NotFoundException.getNotFoundInstance();
    }

    Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), NO_POINTS, BarcodeFormat.MAXICODE);

    String ecLevel = decoderResult.getECLevel();
    if (ecLevel != null) {
      result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
    }
    return result;
  }

  @Override
  public void reset() {
    // do nothing
  }

  /**
   * This method detects a code in a "pure" image -- that is, pure monochrome image
   * which contains only an unrotated, unskewed, image of a code, with some white border
   * around it. This is a specialized method that works exceptionally fast in this special
   * case.
   *
   * @see com.zds.base.code.datamatrix.DataMatrixReader#extractPureBits(BitMatrix)
   * @see com.zds.base.code.qrcode.QRCodeReader#extractPureBits(BitMatrix)
   */
  private static BitMatrix extractPureBits(BitMatrix image) throws NotFoundException {

    int[] enclosingRectangle = image.getEnclosingRectangle();
    if (enclosingRectangle == null) {
      throw NotFoundException.getNotFoundInstance();
    }

    int left = enclosingRectangle[0];
    int top = enclosingRectangle[1];
    int width = enclosingRectangle[2];
    int height = enclosingRectangle[3];

    // Now just read off the bits
    BitMatrix bits = new BitMatrix(MATRIX_WIDTH, MATRIX_HEIGHT);
    for (int y = 0; y < MATRIX_HEIGHT; y++) {
      int iy = top + (y * height + height / 2) / MATRIX_HEIGHT;
      for (int x = 0; x < MATRIX_WIDTH; x++) {
        int ix = left + (x * width + width / 2 + (y & 0x01) *  width / 2) / MATRIX_WIDTH;
        if (image.get(ix, iy)) {
          bits.set(x, y);
        }
      }
    }
    return bits;
  }

}
