/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;

import java.util.LinkedList;
import java.util.List;

public class TestClass7 {

    public synchronized int method(final int number) {
        // force wide opcode use
        int i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18, i19, i20, i21, i22, i23, i24, i25,
                i26, i27, i28, i29, i30, i31, i32, i33, i34, i35, i36, i37, i38, i39, i40, i41, i42, i43, i44, i45, i46, i47, i48, i49,
                i50, i51, i52, i53, i54, i55, i56, i57, i58, i59, i60, i61, i62, i63, i64, i65, i66, i67, i68, i69, i70, i71, i72, i73,
                i74, i75, i76, i77, i78, i79, i80, i81, i82, i83, i84, i85, i86, i87, i88, i89, i90, i91, i92, i93, i94, i95, i96, i97,
                i98, i99, i100, i101, i102, i103, i104, i105, i106, i107, i108, i109, i110, i111, i112, i113, i114, i115, i116, i117,
                i118, i119, i120, i121, i122, i123, i124, i125, i126, i127, i128, i129, i130, i131, i132, i133, i134, i135, i136,
                i137, i138, i139, i140, i141, i142, i143, i144, i145, i146, i147, i148, i149, i150, i151, i152, i153, i154, i155,
                i156, i157, i158, i159, i160, i161, i162, i163, i164, i165, i166, i167, i168, i169, i170, i171, i172, i173, i174,
                i175, i176, i177, i178, i179, i180, i181, i182, i183, i184, i185, i186, i187, i188, i189, i190, i191, i192, i193,
                i194, i195, i196, i197, i198, i199, i200, i201, i202, i203, i204, i205, i206, i207, i208, i209, i210, i211, i212,
                i213, i214, i215, i216, i217, i218, i219, i220, i221, i222, i223, i224, i225, i226, i227, i228, i229, i230, i231,
                i232, i233, i234, i235, i236, i237, i238, i239, i240, i241, i242, i243, i244, i245, i246, i247, i248, i249, i250,
                i251, i252, i253, i254, i255, i256, i257, i258, i259, i260, i261, i262, i263, i264, i265, i266, i267, i268, i269,
                i270, i271, i272, i273, i274, i275, i276, i277, i278, i279, i280, i281, i282, i283, i284, i285, i286, i287, i288,
                i289, i290, i291, i292, i293, i294, i295, i296, i297, i298, i299;

        i299 = 20;

        i299++;

        return i299;
    }

    public static List<Instruction> getResult() {
        final List<Instruction> instructions = new LinkedList<>();

        // constant folding
        instructions.add(new PushInstruction(20, Types.PRIMITIVE_INT));
        instructions.add(new StoreInstruction(300, Types.OBJECT));
        instructions.add(new DefaultInstruction("IINC"));
        instructions.add(new LoadInstruction(300, Types.PRIMITIVE_INT, "i299"));
        instructions.add(new ReturnInstruction());

        return instructions;
    }

}
