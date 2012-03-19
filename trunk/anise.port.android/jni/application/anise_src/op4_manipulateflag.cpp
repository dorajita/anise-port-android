#include "script.h"

#define ASCII_ZERO	0x30

enum ManipulateFlagFunction {
	FLAG_READ = 0,
	FLAG_WRITE = 1,
	FLAG_CALL = 2
};


SCRIPTCALL Script::op4_manipulateFlag()
{
	parameter = getParameter();

	char flag_filename[] = "flag0";
	byte flag_number = (byte) (parameter->get(1) + ASCII_ZERO);
	flag_filename[4] = (char) flag_number;

	word function = parameter->get(0);
	deleteParameter();

	switch (function) {
		case FLAG_READ:
		default:
			{
				byte disabled_status = memory->b_SystemVariable->queryByte(ibf_DisabledStatus);
				disabled_status &= DISABLE_FLAG_RESET;

				MemoryBlock *b_Flag = new MemoryBlock(0, FLAG_SIZE);
				file->open(flag_filename, true);
				file->load(b_Flag, 0, true);
				file->close();
				memory->loadFlag(b_Flag);
				delete b_Flag;

				memory->b_SystemVariable->andByte(ibf_DisabledStatus, DISABLE_FLAG_SET);
				memory->b_SystemVariable->orByte(ibf_DisabledStatus, disabled_status);

				//HACK: check it out
				//memory->b_SystemVariable->writeWord(iwpo_Heap, memory->heap_entry);
				memory->b_SystemVariable->writeWord(iwpo_Selection_Item, option->selection_item_entry);

				//TODO: it is only true for DAT based games like nanpa1 or aisimai
				if (!option->is_unpacked) {
					file->initializeDAT(0, memory->b_SystemVariable->queryByte(ib_PackedFile_Slot0));
					file->initializeDAT(1, memory->b_SystemVariable->queryByte(ib_PackedFile_Slot1));
				}

				char *filename = memory->b_ScriptName->queryString(0, SCRIPT_NAME_SIZE);
				file->open(filename, false);
				file->load(memory->b_Script, 0, false);
				file->close();
				delete filename;

				animation->clearSlot();

				word script_offset = memory->b_SystemVariable->queryWord(iwpo_Script);
				memory->b_Script->set(script_offset + memory->b_Script->queryWord(script_offset));
			}
			break;

		case FLAG_WRITE:
			{
				file->open(flag_filename, true);
				file->store(memory->b_ScriptName);
				file->store(memory->b_Variable);
				file->store(memory->b_BaseVariable);
				file->store(memory->b_SystemVariable);
				file->store(memory->b_Heap);
				file->close();
			}
			break;

		case FLAG_CALL:
			{
				char *script_name = memory->b_ScriptName->queryString(0, SCRIPT_NAME_SIZE);
				memory->b_SystemVariable->andByte(ibf_DisabledStatus, DISABLE_FLAG_RESET);

				MemoryBlock *b_Flag = new MemoryBlock(0, FLAG_SIZE);
				file->open(flag_filename, true);
				file->load(b_Flag, 0, true);
				file->close();
				memory->loadFlag(b_Flag);
				delete b_Flag;

				memory->b_SystemVariable->andByte(ibf_DisabledStatus, DISABLE_FLAG_SET);

				//HACK: check it out
				//memory->b_SystemVariable->writeWord(iwpo_Heap, memory->heap_entry);
				//memory->b_SystemVariable->writeWord(iwpo_Selection_Item, 0xED7C);

				memory->b_ScriptName->writeString(0, script_name, SCRIPT_NAME_SIZE);
				delete script_name;
			}
			break;
	}

	return RETURN_NORMAL;
}
