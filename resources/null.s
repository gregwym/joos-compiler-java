extern java.lang.String_VTABLE


section .text

global __NULL_.NULL_THIS__
	__NULL_.NULL_THIS__:
		ret

global __NULL_.equals_java.lang.Object__
	__NULL_.equals_java.lang.Object__:
		mov eax, [ebp + 12]	; Accessing parameter: __NULL_.equals(java.lang.Object,).other
		cmp eax, 0x0
		je __COMPARISON_TRUE_0
		mov eax, 0x0
		jmp __COMPARISON_FALSE_0
	__COMPARISON_TRUE_0:
		mov eax, 0xffffffff
	__COMPARISON_FALSE_0:
		ret

global __NULL_.toString__
	__NULL_.toString__:
		mov eax, __STRING_0
		ret

global __NULL_.hashCode__
	__NULL_.hashCode__:
		mov eax, 0
		ret

global __NULL_.clone__
	__NULL_.clone__:
		ret

global __NULL_.getClass__
	__NULL_.getClass__:
		mov eax, 0x0
		ret

global __NULL__VTABLE
	__NULL__VTABLE:
		dd -1
		dd __NULL_.NULL_THIS__
		dd __NULL_.equals_java.lang.Object__
		dd __NULL_.toString__
		dd __NULL_.hashCode__
		dd __NULL_.clone__
		dd __NULL_.getClass__


section .data

__STRING_0 dd java.lang.String_VTABLE
dd __STRING_LIT_0
__STRING_LIT_0 dd 4
dd "null"
align 4

global __NULL_LIT_
__NULL_LIT_ dd __NULL__VTABLE
