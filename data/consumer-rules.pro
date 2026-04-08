# Consumer rules shipped with the data AAR.
# Downstream apps need annotation and signature metadata for serialized models.

-keepattributes Signature,*Annotation*

-dontwarn java.lang.invoke.StringConcatFactory
git